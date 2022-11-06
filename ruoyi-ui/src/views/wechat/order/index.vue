<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="门诊科目" prop="courseId">
        <el-select v-model="form.courseId" value-key="id" class="tams-form-item" @change="handleQuery">
          <el-option v-for="item in selectCourseOptions"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="订单单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付方式" prop="paymentWay">
        <el-input
          v-model="queryParams.paymentWay"
          placeholder="请输入支付方式1、货到付款；2、在线支付"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否支付" prop="isPay">
        <el-select v-model="queryParams.isPay" placeholder="请选择是否支付" clearable>
          <el-option
            v-for="dict in dict.type.sys_yes_no"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-input
          v-model="queryParams.status"
          placeholder="请输入订单状态1、待发货 2、待收货 3、确认收货/已完成 5、已关闭"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['wechat:order:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['wechat:order:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['wechat:order:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wechat:order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="微信id" align="center" prop="wxUserId" />
      <el-table-column label="门诊ID" align="center" prop="schedulingId" />
      <el-table-column label="订单单号" align="center" prop="orderNo" />
      <el-table-column label="支付方式" align="center" prop="paymentWay" />
      <el-table-column label="是否支付" align="center" prop="isPay">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.isPay"/>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wechat:order:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wechat:order:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改订单管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="微信id" prop="wxUserId">
          <el-input v-model="form.wxUserId" placeholder="请输入微信id" />
        </el-form-item>
        <el-form-item label="门诊ID" prop="schedulingId">
          <el-input v-model="form.schedulingId" placeholder="请输入门诊ID" />
        </el-form-item>
        <el-form-item label="订单单号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="请输入订单单号" />
        </el-form-item>
        <el-form-item label="支付方式1、货到付款；2、在线支付" prop="paymentWay">
          <el-input v-model="form.paymentWay" placeholder="请输入支付方式1、货到付款；2、在线支付" />
        </el-form-item>
        <el-form-item label="是否支付0、未支付 1、已支付" prop="isPay">
          <el-select v-model="form.isPay" placeholder="请选择是否支付0、未支付 1、已支付">
            <el-option
              v-for="dict in dict.type.sys_yes_no"
              :key="dict.value"
              :label="dict.label"
:value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态1、待发货 2、待收货 3、确认收货/已完成 5、已关闭" prop="status">
          <el-input v-model="form.status" placeholder="请输入订单状态1、待发货 2、待收货 3、确认收货/已完成 5、已关闭" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, getOrder, delOrder, addOrder, updateOrder } from "@/api/wechat/order";
import { listSelectDoctor } from "@/api/doctor/doctor";
import { listSelectCourse } from "@/api/doctor/course";

export default {
  name: "Order",
  dicts: ['sys_yes_no'],
  data() {
    return {
      selectDoctorOptions: [],
      doctorOptions: {},
      selectCourseOptions: [],
      courseOptions: {},
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 订单管理表格数据
      orderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        schedulingId: null,
        orderNo: null,
        paymentWay: null,
        isPay: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  async created() {
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
    this.getList();
  },
  methods: {
    /** 查询订单管理列表 */
    getList() {
      this.loading = true;
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        wxUserId: null,
        schedulingId: null,
        orderNo: null,
        paymentWay: null,
        isPay: null,
        status: null,
        freightPrice: null,
        salesPrice: null,
        paymentPrice: null,
        paymentTime: null,
        deliveryTime: null,
        receiverTime: null,
        closingTime: null,
        userMessage: null,
        transactionId: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        isDeleted: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加订单管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getOrder(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改订单管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrder(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除订单管理编号为"' + ids + '"的数据项？').then(function() {
        return delOrder(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wechat/order/export', {
        ...this.queryParams
      }, `order_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
