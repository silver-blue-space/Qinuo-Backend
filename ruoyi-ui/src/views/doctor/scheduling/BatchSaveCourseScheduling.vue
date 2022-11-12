<template>
  <div>
    <el-dialog title="批量排班" width="850px"
               :close-on-click-modal="false"
               :close-on-press-escape="false"
               :visible.sync="dialogVisible"
               :before-close="handleClose" >
      <el-form id="batch-save-course-scheduling-form" ref="form" :model="form" :rules="rules" label-width="100px" inline class="tams-form-container">
        <el-row>
          <el-col :span="12">
            <el-form-item label="门诊科目" prop="courseId" clearable>
              <el-select v-model="form.courseId" value-key="id" class="tams-form-item"  @change="courseChange" style="width: 250px;"  placeholder="请选择排班门诊科目">
                <el-option v-for="item in selectCourseOptions"
                           :key="item.value"
                           :label="item.label"
                           :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医生姓名" prop="userId" clearable>
              <el-select v-model="form.userId" class="tams-form-item"  placeholder="请选择排班医生">
                <el-option
                  v-for="item in selectDoctorOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="门诊状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择门诊状态" clearable>
                <el-option
                  v-for="dict in dict.type.sys_scheduling_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售价(元)" prop="salesPrice">
              <el-input-number
                v-model="form.salesPrice"
                placeholder="对外售价"
                :min="1"
                :max="888888"
                :precision="2"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="排班日期" prop="dates" >
              <el-date-picker
                :clearable="false"
                v-model="form.dates"
                :picker-options="datesPickerOptions"
                type="daterange"
                range-separator="至" style="width: 250px;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约上限" prop="ticketCount">
              <el-input-number
                v-model="form.ticketCount"
                placeholder="最大可预约人数"
                :min="1"
                :max="100"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="周几上班" prop="weekList">
          <el-checkbox-group v-model="weekList" :min="1" size="small">
            <el-checkbox border v-for="item in weekOptions" :key="item.label" :label="item.label" :checked="item.checked">{{item.name}}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <br/>
        <el-form-item label="门诊开始" prop="attendTime">
          <el-time-select :clearable="false"
                          style="width: 130px"
                          v-model="form.attendTime"
                          :disabled="!(form.courseId&&form.courseId>0)"
                          :picker-options="timePickerOptions"
                          @change="calcFinishTime" placeholder="门诊开始时间">
          </el-time-select>
        </el-form-item>
        <el-form-item label="门诊时长">
          <el-input-number style="width: 130px"
                           v-model="courseDuration"
                           :step="15"
                           :min="0"
                           :max="360"
                           :disabled="!(form.courseId&&form.courseId>0)"
                           @change="calcFinishTime"  placeholder="门诊持续时间"></el-input-number>
        </el-form-item>
        <el-form-item label="门诊结束" prop="finishTime">
          <el-input style="width: 130px" v-model="form.finishTime" :disabled="!(form.courseId&&form.courseId>0)" readonly  placeholder="门诊结束时间"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" :loading="submitBtnLoading" @click="submit">确定</el-button>
      </div>
    </el-dialog>
    <el-dialog width="450px" :title="errMsg" :visible.sync="tipDialogVisible">
      <el-alert v-for="(item, index) in errData" :key="index"
                :title="item" type="error" :closable="false"
                style="margin-bottom: 5px;">
      </el-alert>
    </el-dialog>
  </div>
</template>

<script>
import moment from 'moment'

import { listSelectDoctor } from "@/api/doctor/doctor";
import { listSelectCourse } from "@/api/doctor/course";
import { batchSaveCourseScheduling} from "@/api/doctor/scheduling";

export default {
  name: 'BatchSaveCourseScheduling',
  dicts: ['sys_scheduling_status'],
  props: {
    visible: {
      type: Boolean
    }
  },
  data () {
    return {
      selectDoctorOptions: [],
      selectCourseOptions: [],
      dialogVisible: false,
      tipDialogVisible: false,
      errMsg: '',
      errData: [],
      weekList: [],
      form: {},
      currentCourse: {},
      courseDuration: 0,
      weekOptions: [
        { label: 1, name: '周一' },
        { label: 2, name: '周二' },
        { label: 3, name: '周三' },
        { label: 4, name: '周四' },
        { label: 5, name: '周五' },
        { label: 6, name: '周六' },
        { label: 7, name: '周日' }
      ],
      datesPickerOptions: {
        firstDayOfWeek: 1
      },
      timePickerOptions: {
        start: '07:00',
        step: '00:15',
        end: '20:00'
      },
      rules: {
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
        dates: [
          {
            required: true,
            type: 'array',
            min: 2,
            message: '排班日期不能为空',
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
        ],
        status: [
          {
            required: true,
            message: '门诊状态不能为空',
            trigger: 'blur'
          }
        ],
        salesPrice: [
          {
            required: true,
            message: '对外售价不能为空',
            trigger: 'blur'
          }
        ]
      },
      submitBtnLoading: false
    }
  },
  methods: {
    init () {
      this.weekList = [1, 2, 3, 4, 5]
      this.selectCourseOptions = [];
      listSelectCourse().then(res => {
        if(res && res.data){
          res.data.forEach(p =>{
            this.selectCourseOptions.push({ label: p.name , value: p.id, duration:p.duration });
          })
        }
      });

      this.selectDoctorOptions = [];
      listSelectDoctor().then(res => {
        if(res && res.data){
          res.data.forEach(p =>{
            this.selectDoctorOptions.push({ label: (p.nickName + '('  + (p.dept && p.dept.deptName ? p.dept.deptName + '-':'' ) + p.phonenumber + ')'), value: p.userId });
          })
        }
      });
    },
    resetData () {
      this.$refs.form.resetFields()
      this.form = {}
      this.courseDuration = 0
      this.currentCourse = {}
      this.weekList = [1, 2, 3, 4, 5]
      this.errMsg = ''
      this.errData = []
      this.selectCourseOptions = []
      this.selectDoctorOptions = []
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
          this.form.startDate = moment(this.form.dates[0]).format('YYYY-MM-DD')
          this.form.endDate = moment(this.form.dates[1]).format('YYYY-MM-DD')
          this.form.weekList = this.weekList
          this.form.attendTime = moment(this.form.attendTime, 'HH:mm').format('HH:mm:ss')
          this.form.finishTime = moment(this.form.finishTime, 'HH:mm').format('HH:mm:ss')
          batchSaveCourseScheduling(this.form).then(() => {
            this.submitBtnLoading = false
            this.resetData()
            this.$emit('on-success')
            this.dialogVisible = false
          }).catch((err) => {
            if (err.code === 500) {
              this.errMsg = err.msg
              this.errData = err.data
              this.tipDialogVisible = true
            }
            this.form.attendTime = moment(this.form.attendTime, 'HH:mm:ss').format('HH:mm')
            this.form.finishTime = moment(this.form.finishTime, 'HH:mm:ss').format('HH:mm')
            this.submitBtnLoading = false
          })
        }
      })
    },
    courseChange (val) {
      this.currentCourse = {};
      if(val){
        this.form.courseId = val;
        let filterCourses = this.selectCourseOptions.filter(item => (item.value && val === item.value));
        if (filterCourses && filterCourses.length  && filterCourses[0].duration > 0) {
          this.currentCourse = { ...filterCourses[0] };
          this.courseDuration = this.currentCourse.duration
          if (this.form.attendTime) {
            this.$set(this.form, 'finishTime', moment(this.form.attendTime, 'HH:mm').add(this.courseDuration, 'm').format('HH:mm'))
          }
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

<style>
#batch-save-course-scheduling-form .el-checkbox {
  color: #606266;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  margin-right: 6px;
  line-height: 28px;
}

#batch-save-course-scheduling-form .el-checkbox.is-bordered.el-checkbox--small {
  padding: 5px 15px 5px 10px;
  border-radius: 3px;
  height: 40px;
}
</style>
