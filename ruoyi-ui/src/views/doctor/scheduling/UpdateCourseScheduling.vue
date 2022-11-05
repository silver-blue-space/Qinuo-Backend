<template>
  <el-dialog title="门诊排班修改" width="790px"
             :close-on-click-modal="false"
             :close-on-press-escape="false"
             :visible.sync="dialogVisible"
             :before-close="handleClose" >
    <el-form ref="form" :model="form" :rules="rules" inline label-width="80px" class="tams-form-container">
      <el-form-item label="日期" prop="schedulDate">
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
                         @change="calcFinishTime"></el-input-number>
      </el-form-item>
      <el-form-item prop="finishTime">
        <el-input v-model="form.finishTime"
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
import { getScheduling,updateScheduling } from "@/api/doctor/scheduling";
import { listSelectDoctor } from "@/api/doctor/doctor";
import { listSelectCourse } from "@/api/doctor/course";

export default {
  name: 'UpdateCourseScheduling',
  props: {
    visible: {
      type: Boolean
    },
    id: [String, Number],
  },
  data () {
    return {
      selectDoctorOptions: [],
      doctorOptions: {},
      selectCourseOptions: [],
      courseOptions: {},
      dialogVisible: false,
      classroomData: [],
      courseData: [],
      teacherData: [],
      form: {},
      currentCourse: {},
      courseDuration: 0,
      attendTimeSelectKey: new Date().getTime(),
      datesPickerOptions: {
        firstDayOfWeek: 1
      },
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
      //排班详情
      await getScheduling(this.id).then(res => {
        if (res) {
          this.form = res.data;
          this.form.attendTime = moment(this.form.attendTime, 'HH:mm:ss').format('HH:mm')
          this.form.finishTime = moment(this.form.finishTime, 'HH:mm:ss').format('HH:mm')
          this.courseDuration = this.form.duration
          //初始化 门诊时长
          this.courseChange(res.data.courseId);
        }
      }).catch(() => {
      });
      //
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
          updateScheduling(this.form).then(() => {
            this.submitBtnLoading = false
            this.$refs.form.resetFields()
            this.$emit('on-success')
            this.dialogVisible = false
          }).catch(() => {
            this.form.attendTime = moment(this.form.attendTime, 'HH:mm:ss').format('HH:mm')
            this.form.finishTime = moment(this.form.finishTime, 'HH:mm:ss').format('HH:mm')
            this.submitBtnLoading = false
          });
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
