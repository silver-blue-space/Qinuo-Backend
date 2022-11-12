<template>
  <div class="app-container">
    <!-- search area -->
    <el-row>
      <el-col :span="20">
        <el-form inline>
          <el-form-item label="门诊科目" prop="courseId">
            <el-select ref="courseSelect" clearable  style="width: 250px;" v-model="params.courseId" placeholder="请选择门诊科目" @change="search">
              <el-option v-for="item in selectCourseOptions"
                         :key="item.value"
                         :label="item.label"
                         :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="医生姓名" prop="userId">
            <el-select ref="doctorSelect" clearable style="width: 250px;" v-model="params.userId" placeholder="请选择医生" @change="search">
              <el-option
                v-for="item in selectDoctorOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" size="small" style="float: right" @click="exportCourseSchedulingVisible=true">导出</el-button>
        <el-button type="primary" size="small" style="float: right; margin-right: 10px;" @click="saveBatchCourseSchedulingVisible=true">排班</el-button>
      </el-col>
    </el-row>
    <!--Calender area -->
    <FullCalendar
      v-if="selectDoctorOptions && selectCourseOptions"
      ref="fullCalendarRef"
      :options="calendarOptions">
    </FullCalendar>
    <!--批量排班 area -->
    <BatchSaveCourseScheduling :visible="saveBatchCourseSchedulingVisible"
                               @on-close="saveBatchCourseSchedulingVisible=false"
                               @on-success="saveBatchSuccess"></BatchSaveCourseScheduling>
    <!--排班详情 area -->
    <ViewCourseScheduling
      v-if="doctorOptions && courseOptions"
      :visible="viewCourseSchedulingVisible"
      :id="id"
      :doctors="doctorOptions"
      :courses="courseOptions"
      @on-success="updateSuccess"
      @on-close="viewCourseSchedulingVisible=false">
    </ViewCourseScheduling>
    <!--新建排班 area -->
    <SaveCourseScheduling :visible="saveCourseSchedulingVisible"
                          :date="currentDate"
                          :attendTime="currentAttendTime"
                          @on-close="saveCourseSchedulingVisible=false"
                          @on-success="saveSuccess">
    </SaveCourseScheduling>
  </div>
</template>

<script>
import FullCalendar from '@fullcalendar/vue'
import dayGridPlugin from '@fullcalendar/daygrid'
import listPlugin from '@fullcalendar/list'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import moment from 'moment'

import { listSelectDoctor } from "@/api/doctor/doctor";
import { listSelectCourse } from "@/api/doctor/course";
import { listCalendarSchedulingList ,updateScheduling} from "@/api/doctor/scheduling";
import BatchSaveCourseScheduling from '@/views/doctor/scheduling/BatchSaveCourseScheduling'
import ViewCourseScheduling from '@/views/doctor/scheduling/ViewCourseScheduling'
import SaveCourseScheduling from '@/views/doctor/scheduling/SaveCourseScheduling'



export default {
  name: "Scheduling",
  components: {
    FullCalendar,
    BatchSaveCourseScheduling,
    ViewCourseScheduling,
    SaveCourseScheduling,
  },
  data() {
    return {
      courseCountObj: {},
      weekList: [],
      classroomData: [],
      courseData: [],
      teacherData: [],
      params: {},
      saveCourseSchedulingVisible: false,
      viewCourseSchedulingVisible: false,
      saveBatchCourseSchedulingVisible: false,
      exportCourseSchedulingVisible: false,
      id: 0,
      currentDate: '',
      currentAttendTime: '',
      calendarOptions: {
        plugins: [dayGridPlugin, listPlugin, timeGridPlugin, interactionPlugin],
        initialView: 'timeGridWeek',
        locale: 'zh',
        // 每周开始是周几，Sunday=0, Monday=1
        firstDay: '1',
        // 时间轴间距
        slotMinTime: '07:00',
        slotMaxTime: '21:00',
        slotDuration: '00:15:00',
        // 是否显示第几周
        weekNumbers: true,
        weekText: '周',
        // 是否显示当前时间标记
        nowIndicator: true,
        // 是否显示全天插槽
        allDaySlot: false,
        // 日期否可点击
        navLinks: true,
        // 月视图，是否为指定周数高度，true 6周高度
        fixedWeekCount: false,
        // 月视图，是否显示非本月日期
        showNonCurrentDates: false,
        // 月视图，限制每天显示最大事件数，不包括+more链接，false 全部显示，true 限制为日单元格的高度，number 限制为指定行数高度
        dayMaxEvents: true,
        // 与dayMaxEvents类似，区别为包括+more链接
        // dayMaxEventRows: true,
        // 是否可拖拽
        editable: true,
        events: [],
        headerToolbar: {
          start: 'prev,next today',
          center: 'title',
          end: 'timeGridWeek,dayGridMonth'
        },
        buttonText: {
          today: '今天',
          month: '月',
          week: '周',
          day: '日',
          list: '周列表'
        },
        slotLabelFormat: {
          hour: 'numeric',
          minute: '2-digit',
          hour12: false
        },
        eventTimeFormat: {
          hour: 'numeric',
          minute: '2-digit',
          hour12: false
        },
        datesSet: this.datesSet,
        dayHeaderContent: this.dayHeaderContent,
        eventClick: this.handleEventClick,
        dateClick: this.handleDateClick,
        // 当事件拖动时触发
        eventDragStart: this.eventDragStart,
        // 当事件拖动停止时触发
        eventDragStop: this.eventDragStop,
        // 当拖动停止并且事件移动到不同的日子/时间时触发
        eventDrop: this.eventDrop,
        // 当外部可拖动元素或来自另一个日历的事件被拖放到该日历中时调用
        drop: this.drop,
        // 当带有关联事件数据的外部可拖动元素拖放到日历中时调用，或来自另一个日历的事件
        eventReceive: this.eventReceive,
        // 当一个日历上的事件即将拖放到另一个日历上时触发
        eventLeave: this.eventLeave,
        // 开始缩放时触发
        eventResizeStart: this.eventResizeStart,
        // 停止缩放时触发
        eventResizeStop: this.eventResizeStop,
        // 当缩放停止且事件持续时间发生更改时触发
        eventResize: this.eventResize
      },
      //
      selectDoctorOptions: [],
      doctorOptions: {},
      selectCourseOptions: [],
      courseOptions: {},

    };
  },
   created() {
     this.init();
  },
  computed: {
  },
  async mounted () {
    await this.init();
    //在mounted 声明周期中创建定时器
    const timer = setInterval(()=>{
      // 这里调用调用需要执行的方法，1为自定义的参数，由于特殊的需求它将用来区分，定时器调用和手工调用，然后执行不同的业务逻辑
      this.$cache.session.set("schedulingStartDate",'');
      this.$cache.session.set("schedulingEndDate",'');
      this.search();
    }, 1000) // 每两秒执行1次
    // 通过$once来监听定时器，在beforeDestroy钩子可以被清除
    this.$once('hook:beforeDestroy',()=>{
      // 在页面销毁时，销毁定时器
      clearInterval(timer)
    });
  },
  methods: {
    async init(){
      // 查询医生列表数据
      await this.getDoctorList();
      // 查询门诊科目列表(下拉列表)
      await this.getCourseList();
      // 查询排班日历数据
      await this.search();
    },
    /** 批量排班更新成功排班列表 */
    saveBatchSuccess(){
      this.search()
      this.saveBatchCourseSchedulingVisible = false
    },
    /** 查询排班列表 */
    search(){
      if(!this.$refs.fullCalendarRef){
        return;
      }
      let startDate = moment(this.$refs.fullCalendarRef.getApi().view.currentStart).format('YYYY-MM-DD');
      let endDate = moment(this.$refs.fullCalendarRef.getApi().view.currentEnd).format('YYYY-MM-DD');
      let cacheStartDate =  this.$cache.session.get("schedulingStartDate") || '';
      let cacheEndDate =  this.$cache.session.get("schedulingEndDate") || '';
      if(cacheStartDate  && cacheStartDate === startDate  &&
        cacheEndDate && cacheEndDate === endDate){
        return;
      }
      listCalendarSchedulingList(this.addDateRange(this.params, [startDate,endDate])).then(res => {
        if (res && res.data) {
          this.calendarOptions.events = [];
          this.courseCountObj = res.scheduleCount || {};
          res.data.forEach(item => {
            this.calendarOptions.events.push({
              id: item.id,
              title: this.courseOptions[item.courseId] + ' '  + this.doctorOptions[item.userId],
              start: item.schedulDate + ' ' + item.attendTime,
              end: item.schedulDate + ' ' + item.finishTime,
              extendedProps: item,
              backgroundColor: item.backgroundColor,
            })
          })
          this.$cache.session.set("schedulingStartDate",startDate);
          this.$cache.session.set("schedulingEndDate",endDate);
        }
      }).catch(() => {
      })
    },
    /** 查询医生列表(下拉列表) */
    async getDoctorList() {
      this.selectDoctorOptions = [];
      let res =  await listSelectDoctor();
      if(res && res.data){
        res.data.forEach(p =>{
          if(!this.doctorOptions[p.userId]){
            this.doctorOptions[p.userId] = p.nickName;
            this.selectDoctorOptions.push({ label: (p.nickName + '('  + (p.dept && p.dept.deptName ? p.dept.deptName + '-':'' ) + p.phonenumber + ')'), value: p.userId });
          }
        });
      }
    },
    /** 查询门诊科目列表(下拉列表) */
    async getCourseList() {
      this.selectCourseOptions = [];
      let res = await listSelectCourse();
      if(res && res.data){
        res.data.forEach(p =>{
          if(!this.courseOptions[p.id]){
            this.courseOptions[p.id] = p.name;
            this.selectCourseOptions.push({ label: p.name , value: p.id });
          }
        });
      }
    },
    ////////
    updateSuccess () {
      this.search()
      this.viewCourseSchedulingVisible = false
    },
    datesSet (info) {
      this.search()
    },
    saveSuccess () {
      this.search()
      this.saveCourseSchedulingVisible = false
    },
    dayHeaderContent (info) {
      if (info.view.type === 'dayGridMonth') {
        return { html: `<div class="fc-scrollgrid-sync-inner"><a class="fc-col-header-cell-cushion">${info.text}</a></div>` }
      } else if (info.view.type === 'timeGridWeek') {
        return {
          html: `<div class="fc-scrollgrid-sync-inner">
                    <a class="fc-col-header-cell-cushion"
                        data-navlink="{&quot;date&quot;:&quot;${moment(info.date).format('YYYY-MM-DD')}&quot;,&quot;type&quot;:&quot;day&quot;}" tabindex="0">${info.text}</a>
                    <span id="tams-course-count-${moment(info.date).format('YYYY-MM-DD')}" style="cursor: default;">${this.getCourseCount(info.date)}</span>排班
                </div>`
        }
      } else if (info.view.type === 'dayGridDay') {
        return {
          html: `<div class="fc-scrollgrid-sync-inner">
                     <a class="fc-col-header-cell-cushion">${info.text}</a>
                     <span id="tams-course-count-${moment(info.date).format('YYYY-MM-DD')}" style="cursor: default;">${this.getCourseCount(info.date)}</span>排班
                 </div>`
        }
      } else if (info.view.type === 'listWeek') {
        return {
          html: `<a class="fc-list-day-text"
                      data-navlink="${info.navLinkData.replace(/"/g, '&quot;')}" tabindex="0">${info.text}</a>
                 <a class="fc-list-day-side-text"
                      data-navlink="${info.navLinkData.replace(/"/g, '&quot;')}" tabindex="0">${info.sideText}</a>`
        }
      }
      return 'unknown view type'
    },
    getCourseCount (date) {
      const count = this.courseCountObj[moment(date).format('YYYY-MM-DD')]
      return count || 0
    },
    /** 查看詳情 */
    handleEventClick (info) {
      this.id = info.event.id
      this.viewCourseSchedulingVisible = true
    },
    handleDateClick (info) {
      this.currentDate = moment(info.date).format('YYYY-MM-DD')
      const infoDate = moment(info.date)
      if (infoDate.hour() > 0) {
        this.currentAttendTime = infoDate.format('HH:mm')
      }
      this.saveCourseSchedulingVisible = true
    },
    eventDragStart (info) {
      console.log('eventDragStart', info)
    },
    eventDragStop (info) {
      console.log('eventDragStop', info)
    },
    eventDrop (info) {
      //拖拽
      updateScheduling({
        id: info.event.id,
        schedulDate: moment(info.event.start).format('YYYY-MM-DD'),
        attendTime: moment(info.event.start).format('HH:mm:ss'),
        finishTime: moment(info.event.end).format('HH:mm:ss')
      }).then(res => {
        this.search()
      }).catch(() => {
        this.search()
      })
    },
    drop (info) {
      console.log('drop', info)
    },
    eventReceive (info) {
      console.log('eventReceive', info)
    },
    eventLeave (info) {
      console.log('eventLeave', info)
    },
    eventResizeStart (info) {
      console.log('eventResizeStart', info)
    },
    eventResizeStop (info) {
      console.log('eventResizeStop', info)
    },
    eventResize (info) {
      //拖拽
      updateScheduling({
        id: info.event.id,
        schedulDate: moment(info.event.start).format('YYYY-MM-DD'),
        attendTime: moment(info.event.start).format('HH:mm:ss'),
        finishTime: moment(info.event.end).format('HH:mm:ss')
      }).then(res => {
        this.search()
      }).catch(() => {
        this.search()
      })
    },
  }
};
</script>
