<template>
  <div>
    <el-dialog title="门诊排班详情" width="790px"
               :visible.sync="dialogVisible"
               :before-close="handleClose" >
      <el-form ref="form" :model="form" inline label-width="80px" class="tams-form-container">
        <el-form-item label="日期">
          <el-input v-model="form.schedulDate" class="tams-form-item" readonly></el-input>
        </el-form-item>
        <el-form-item label="预约上限">
          <el-input v-model="form.ticketCount" class="tams-form-item" readonly></el-input>
        </el-form-item>
        <el-form-item label="门诊科目">
          <el-input v-model="courses[form.courseId]" class="tams-form-item" readonly></el-input>
        </el-form-item>
        <el-form-item label="医生姓名">
          <el-input v-model="doctors[form.userId]" class="tams-form-item" readonly></el-input>
        </el-form-item>
        <el-form-item label="门诊开始">
          <el-input v-model="form.attendTime" class="tams-form-item" readonly></el-input>
        </el-form-item>
        <el-form-item label="门诊结束">
          <el-input v-model="form.finishTime" class="tams-form-item" readonly></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="danger" slot="reference" style="margin-right: 10px;" @click="remove">删除</el-button>
        <el-button type="primary" @click="updateCourseSchedulingVisible=true">修改</el-button>
      </div>
    </el-dialog>
    <UpdateCourseScheduling
      :visible="updateCourseSchedulingVisible"
      :id="id"
      @on-success="updateSuccess"
      @on-close="updateCourseSchedulingVisible=false"></UpdateCourseScheduling>
  </div>
</template>

<script>
import moment from 'moment'
import UpdateCourseScheduling from '@/views/doctor/scheduling/UpdateCourseScheduling'
import { getScheduling,delScheduling } from "@/api/doctor/scheduling";


export default {
  name: 'ViewCourseScheduling',
  components: { UpdateCourseScheduling },
  props: {
    visible: {
      type: Boolean
    },
    id: [Number, String],
    doctors:{
      type: Object,
      default() {
        return {};
      }
    },
    courses:{
      type: Object,
      default() {
        return {};
      }
    },
  },
  data () {
    return {
      dialogVisible: false,
      updateCourseSchedulingVisible: false,
      form: {}
    }
  },
  methods: {
    search () {
      getScheduling(this.id).then((res) => {
        this.form = res.data;
        this.form.attendTime = moment(res.data.attendTime, 'HH:mm:ss').format('HH:mm')
        this.form.finishTime = moment(res.data.finishTime, 'HH:mm:ss').format('HH:mm')
      }).catch(() => {
      })
    },
    remove () {
      this.$confirm('此操作将永久删除该门诊排班, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delScheduling(this.id).then((res) => {
          this.$emit('on-success')
          this.dialogVisible = false
        }).catch(() => {
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });

    },
    handleClose (done) {
      this.$refs.form.resetFields()
      this.$emit('on-close')
      done()
    },
    close () {
      this.$refs.form.resetFields()
      this.$emit('on-close')
      this.dialogVisible = false
    },
    updateSuccess () {
      this.$emit('on-success')
      this.search()
      this.updateCourseSchedulingVisible = false
    }
  },
  watch: {
    visible (val) {
      if (val) {
        this.search()
        this.dialogVisible = val
      }
    }
  }
}
</script>
