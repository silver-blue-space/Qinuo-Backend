<template>
  <el-dialog title="门诊排班新建" width="790px"
             :close-on-click-modal="false"
             :close-on-press-escape="false"
             :visible.sync="dialogVisible"
             :before-close="handleClose" >
    <el-form ref="form" :model="form" :rules="rules" inline label-width="80px" class="tams-form-container">
      <el-form-item label="排班日期" prop="schedulDate">
        <el-date-picker v-model="form.schedulDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :clearable="false"
                        :picker-options="datesPickerOptions"
                        class="tams-form-item"></el-date-picker>
      </el-form-item>
      <el-form-item label="预约上限" prop="ticketCount">
        <el-input-number
          v-model="form.ticketCount"
          placeholder="最大可预约人数"
          :min="1"
          :max="100"
          clearable
        />
      </el-form-item>
      <br/>
      <el-form-item label="门诊科目" prop="courseId">
        <el-select v-model="form.courseId" value-key="id" class="tams-form-item" @change="courseChange">
          <el-option v-for="item in selectCourseOptions"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="医生姓名" prop="userId">
        <el-select v-model="form.userId" class="tams-form-item">
          <el-option
            v-for="dict in selectDoctorOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="时间" prop="attendTime">
        <el-time-select :clearable="false"
                        style="width: 210px;"
                        v-model="form.attendTime"
                        :disabled="!(form.courseId&&form.courseId>0)"
                        :picker-options="pickerOptions"
                        :key="attendTimeSelectKey"
                        @change="calcFinishTime">
        </el-time-select>
      </el-form-item>
      <el-form-item>
        <el-input-number v-model="courseDuration"
                         style="width: 150px;"
                         :step="15"
                         :min="0"
                         :max="360"
                         :disabled="!(form.courseId&&form.courseId>0)"
                         @change="calcFinishTime"></el-input-number>
      </el-form-item>
      <el-form-item prop="finishTime">
        <el-input v-model="form.finishTime"
                  :disabled="!(form.courseId&&form.courseId>0)"
                  readonly
                  style="width: 210px;"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取消</el-button>
      <el-button type="primary" :loading="submitBtnLoading" @click="submit">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import moment from 'moment'
import { listSelectDoctor } from "@/api/doctor/doctor";
import { listSelectCourse } from "@/api/doctor/course";
import { addScheduling } from "@/api/doctor/scheduling";

export default {
  name: 'SaveCourseScheduling',
  props: {
    visible: {
      type: Boolean
    },
    date: String,
    attendTime: String
  },
  data () {
    return {
      selectDoctorOptions: [],
      doctorOptions: {},
      selectCourseOptions: [],
      courseOptions: {},
      datesPickerOptions: {
        firstDayOfWeek: 1
      },
      dialogVisible: false,
      classroomData: [],
      courseData: [],
      teacherData: [],
      form: {},
      currentCourse: {},
      courseDuration: 0,
      attendTimeSelectKey: new Date().getTime(),
      pickerOptions: {
        start: '07:00',
        step: '00:15',
        end: '20:00'
      },
      rules: {
        schedulDate: [
          {
            required: true,
            message: '日期不能为空',
            trigger: 'blur'
          }
        ],
        ticketCount : [
          {
            required: true,
            message: '最大预约人数不能为空',
            trigger: 'blur'
          }
        ],
        courseId: [
          {
            required: true,
            message: '门诊科目不能为空',
            trigger: 'blur'
          }
        ],
        userId: [
          {
            required: true,
            message: '医生不能为空',
            trigger: 'blur'
          }
        ],
        attendTime: [
          {
            required: true,
            message: '门诊开始时间不能为空',
            trigger: 'blur'
          }
        ],
        finishTime: [
          {
            required: true,
            message: '门诊结束时间不能为空',
            trigger: 'blur'
          }
        ]
      },
      submitBtnLoading: false
    }
  },
  methods: {
    async init () {
      this.$set(this.form, 'schedulDate', this.date)
      this.$set(this.form, 'attendTime', this.attendTime)
      this.attendTimeSelectKey = new Date().getTime()

      this.selectDoctorOptions = [];
      await  listSelectDoctor().then(res => {
        if(res && res.data){
          res.data.forEach(p =>{
            this.doctorOptions[p.userId] = p.nickName;
            this.selectDoctorOptions.push({ label: (p.nickName + '('  + (p.dept && p.dept.deptName ? p.dept.deptName + '-':'' ) + p.phonenumber + ')'), value: p.userId });
          });
        }
      });
      this.selectCourseOptions = [];
      await listSelectCourse().then(res =>{
        if(res && res.data){
          res.data.forEach(p =>{
            this.courseOptions[p.id] = p.name;
            this.selectCourseOptions.push({ label: p.name , value: p.id,duration:p.duration });
          });
        }
      });
    },
    resetData () {
      this.$refs.form.resetFields()
      this.form = {}
      this.courseDuration = 0
      this.currentCourse = {}
      this.classroomData = []
      this.courseData = []
      this.teacherData = []
    },
    handleClose (done) {
      this.resetData()
      this.$emit('on-close')
      done()
    },
    close () {
      this.resetData()
      this.$emit('on-close')
      this.dialogVisible = false
    },
    submit () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitBtnLoading = true
          this.form.attendTime = moment(this.form.attendTime, 'HH:mm').format('HH:mm:ss')
          this.form.finishTime = moment(this.form.finishTime, 'HH:mm').format('HH:mm:ss')
          addScheduling(this.form).then(() => {
            this.submitBtnLoading = false
            this.resetData()
            this.$emit('on-success')
            this.dialogVisible = false
          }).catch(() => {
            this.form.attendTime = moment(this.form.attendTime, 'HH:mm:ss').format('HH:mm')
            this.form.finishTime = moment(this.form.finishTime, 'HH:mm:ss').format('HH:mm')
            this.submitBtnLoading = false
          })
        }
      })
    },
    courseChange (val) {
      this.form.courseId = val || '';
      let filterCourses = this.selectCourseOptions.filter(item => (item.value && val === item.value));
      if (val && filterCourses && filterCourses.length  && filterCourses[0].duration > 0) {
        this.currentCourse = { ...filterCourses[0] };
        this.courseDuration = this.currentCourse.duration
        if (this.form.attendTime) {
          this.$set(this.form, 'finishTime', moment(this.form.attendTime, 'HH:mm').add(this.courseDuration, 'm').format('HH:mm'))
        }
      }
    },
    calcFinishTime () {
      if (this.form.attendTime) {
        this.$set(this.form, 'finishTime', moment(this.form.attendTime, 'HH:mm').add(this.courseDuration, 'm').format('HH:mm'))
      }
    }
  },
  watch: {
    visible (val) {
      if (val) {
        this.init()
        this.dialogVisible = val
      }
    }
  }
}
</script>
