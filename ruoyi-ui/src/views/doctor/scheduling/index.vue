<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="accountId">
        <el-input
          v-model="queryParams.accountId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="医院科室ID" prop="deptId">
        <el-input
          v-model="queryParams.deptId"
          placeholder="请输入医院科室ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="值班日期" prop="schedulDate">
        <el-date-picker clearable
          v-model="queryParams.schedulDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择值班日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="早中晚班" prop="ampm">
        <el-select v-model="queryParams.ampm" placeholder="请选择上午/下午/夜晚" clearable>
          <el-option
            v-for="dict in sysAmpmOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="时段" prop="period">
        <el-date-picker clearable
          v-model="queryParams.period"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择时段">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="门诊类别" prop="clinicType">
        <el-select v-model="queryParams.clinicType" placeholder="请选择门诊类别" clearable>
          <el-option
            v-for="dict in sysClinicTypeOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="可约人数" prop="ticketCount">
        <el-input
          v-model="queryParams.ticketCount"
          placeholder="请输入可预约患者数"
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
          v-hasPermi="['doctor:scheduling:add']"
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
          v-hasPermi="['doctor:scheduling:edit']"
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
          v-hasPermi="['doctor:scheduling:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['doctor:scheduling:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="schedulingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="用户ID" align="center" prop="accountId" />
      <el-table-column label="医院科室ID" align="center" prop="deptId" />
      <el-table-column label="值班日期" align="center" prop="schedulDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.schedulDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="上午/下午/夜晚" align="center" prop="ampm">
        <template slot-scope="scope">
          <dict-tag :options="sysAmpmOptions" :value="scope.row.ampm"/>
        </template>
      </el-table-column>
      <el-table-column label="时段" align="center" prop="period" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.period, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="门诊类别" align="center" prop="clinicType">
        <template slot-scope="scope">
          <dict-tag :options="sysClinicTypeOptions" :value="scope.row.clinicType"/>
        </template>
      </el-table-column>
      <el-table-column label="状态，" align="center" prop="status" />
      <el-table-column label="可预约患者数" align="center" prop="ticketCount" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['doctor:scheduling:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['doctor:scheduling:remove']"
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

    <!-- 添加或修改排班管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="accountId">
          <el-input v-model="form.accountId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="医院科室ID" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入医院科室ID" />
        </el-form-item>
        <el-form-item label="值班日期" prop="schedulDate">
          <el-date-picker clearable
            v-model="form.schedulDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择值班日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="早中晚班" prop="ampm">
          <el-select v-model="form.ampm" placeholder="请选择上午/下午/夜晚">
            <el-option
              v-for="dict in sysAmpmOptions"
              :key="dict.value"
              :label="dict.label"
:value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时段" prop="period">
          <el-date-picker clearable
            v-model="form.period"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择时段">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="门诊类别" prop="clinicType">
          <el-select v-model="form.clinicType" placeholder="请选择门诊类别：临时停诊、普通门诊，专家门诊， 特需门诊">
            <el-option
              v-for="dict in sysClinicTypeOptions"
              :key="dict.value"
              :label="dict.label"
:value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="可预约患者数" prop="ticketCount">
          <el-input v-model="form.ticketCount" placeholder="请输入可预约患者数" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listScheduling, getScheduling, delScheduling, addScheduling, updateScheduling } from "@/api/doctor/scheduling";
import { getDicts } from '@/api/system/dict/data'

export default {
  name: "Scheduling",
  data() {
    return {
      sysClinicTypeOptions: [],
      sysAmpmOptions: [],
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
      // 排班管理表格数据
      schedulingList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        accountId: null,
        deptId: null,
        schedulDate: null,
        ampm: null,
        period: null,
        clinicType: null,
        status: null,
        ticketCount: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        accountId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        deptId: [
          { required: true, message: "医院科室ID不能为空", trigger: "blur" }
        ],
        schedulDate: [
          { required: true, message: "值班日期不能为空", trigger: "blur" }
        ],
        ampm: [
          { required: true, message: "上午/下午/夜晚不能为空", trigger: "change" }
        ],
        period: [
          { required: true, message: "时段不能为空", trigger: "blur" }
        ],
        clinicType: [
          { required: true, message: "门诊类别：临时停诊、普通门诊，专家门诊， 特需门诊不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "状态，不能为空", trigger: "blur" }
        ],
        ticketCount: [
          { required: true, message: "可预约患者数不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.sysAmpmOptions = [];
    this.sysClinicTypeOptions = [];
    getDicts("sys_clinic_type").then(response => {
      response.data.forEach(p =>{
        this.sysClinicTypeOptions.push({ label: p.dictLabel, value: p.dictValue, elTagType: p.listClass, elTagClass: p.cssClass });
      })
    });
    getDicts("sys_ampm").then(response => {
      response.data.forEach(p =>{
        this.sysAmpmOptions.push({ label: p.dictLabel, value: p.dictValue, elTagType: p.listClass, elTagClass: p.cssClass });
      })
    });
  },
  methods: {
    /** 查询排班管理列表 */
    getList() {
      this.loading = true;
      listScheduling(this.queryParams).then(response => {
        this.schedulingList = response.rows;
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
        accountId: null,
        deptId: null,
        schedulDate: null,
        ampm: null,
        period: null,
        clinicType: null,
        status: "0",
        ticketCount: null,
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
      this.title = "添加排班管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getScheduling(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改排班管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateScheduling(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addScheduling(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除排班管理编号为"' + ids + '"的数据项？').then(function() {
        return delScheduling(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('doctor/scheduling/export', {
        ...this.queryParams
      }, `scheduling_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
