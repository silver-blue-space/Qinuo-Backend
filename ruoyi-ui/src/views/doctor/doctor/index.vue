<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="医生" prop="sysUserId">
        <el-select v-model="queryParams.sysUserId" placeholder="请选择医生" @chage="handleQuery" clearable>
          <el-option
            v-for="item in selectDoctorOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="职称" prop="title">
        <el-select v-model="queryParams.title" placeholder="请选择职称" clearable>
          <el-option
            v-for="dict in dict.type.sys_doctor_title"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="宣传展示" prop="showFlg">
        <el-select v-model="queryParams.showFlg" placeholder="请选择宣传展示" clearable>
          <el-option
            v-for="dict in dict.type.sys_yes_no"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="停启状态" prop="enableState">
        <el-select v-model="queryParams.enableState" placeholder="请选择停启状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['doctor:doctor:add']"
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
          v-hasPermi="['doctor:doctor:edit']"
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
          v-hasPermi="['doctor:doctor:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['doctor:doctor:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="doctorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="医生姓名" align="center" prop="sysUserId" >
        <template slot-scope="scope">
          {{doctorOptions[scope.row.sysUserId] || scope.row.sysUserId}}
        </template>
      </el-table-column>
      <el-table-column label="职称" align="center" prop="title">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_doctor_title" :value="scope.row.title"/>
        </template>
      </el-table-column>
      <el-table-column label="医生介绍" align="center" prop="introduce" >
        <template slot-scope="scope">
          <el-popover
            placement="top-start"
            title="医生介绍"
            width="400"
            trigger="hover"
            :content="scope.row.introduce">
            <el-button slot="reference" size='mini'  class="el-icon-search">查看简介</el-button>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="擅长领域" align="center" prop="expertise" >
        <template slot-scope="scope">
          <el-popover
            placement="top-start"
            title="擅长领域"
            width="600"
            trigger="hover"
            :content="scope.row.expertise">
            <el-button slot="reference" size='mini' class="el-icon-search">查看简介</el-button>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="宣传展示" align="center" prop="showFlg">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.showFlg"/>
        </template>
      </el-table-column>
      <el-table-column label="宣传展示顺序" align="center" prop="showOrder" />
      <el-table-column label="停启状态" align="center" prop="enableState">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.enableState"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['doctor:doctor:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['doctor:doctor:remove']"
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

    <!-- 添加或修改医生管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="系统用户" prop="sysUserId">
          <el-select v-model="form.sysUserId" placeholder="请选择系统用户" style="width:100%" clearable>
            <el-option
              v-for="dict in allUserListOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-row >
          <el-col :span="12">
            <el-form-item label="职称" prop="title">
              <el-select v-model="form.title" placeholder="请选择职称">
                <el-option
                  v-for="dict in dict.type.sys_doctor_title"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="停启状态" prop="enableState">
              <el-select v-model="form.enableState" placeholder="请选择停启状态">
                <el-option
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row >
          <el-col :span="12">
            <el-form-item label="宣传展示" prop="showFlg">
              <el-select v-model="form.showFlg" placeholder="请选择宣传展示">
                <el-option
                  v-for="dict in dict.type.sys_yes_no"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="展示顺序" prop="showOrder" :rules="{
          required: (form.showFlg && form.showFlg === 'Y') , message: '请输入展示顺序(数字越小越靠前)', trigger: 'blur'
        }">
              <el-input v-model="form.showOrder" placeholder="请输入宣传展示顺序"  type="number" style="width: 230px"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="医生介绍" prop="introduce" :rules="{
          required: (form.showFlg && form.showFlg === 'Y') , message: '请输入展示顺序(数字越小越靠前)', trigger: 'blur'
        }">
          <el-input v-model="form.introduce" type="textarea" placeholder="请输入内容" rows="5"/>
        </el-form-item>
        <el-form-item label="擅长领域" prop="expertise" :rules="{
          required: (form.showFlg && form.showFlg === 'Y') , message: '请输入展示顺序(数字越小越靠前)', trigger: 'blur'
        }">
          <el-input v-model="form.expertise" type="textarea" placeholder="请输入内容" rows="5"/>
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
import { listDoctor, getDoctor, delDoctor, addDoctor, updateDoctor,listSelectDoctor } from "@/api/doctor/doctor";
import {listUser}  from "@/api/system/user";

export default {
  name: "Doctor",
  dicts: ['sys_yes_no', 'sys_normal_disable', 'sys_doctor_title'],
  data() {
    return {
      doctorOptions: {},
      allUserListOptions: [],
      selectDoctorOptions: [],
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
      // 医生管理表格数据
      doctorList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        sysUserId: null,
        title: null,
        showFlg: null,
        enableState: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "医生职称不能为空", trigger: "blur" }
        ],
        sysUserId: [
          { required: true, message: "用户id不能为空", trigger: "blur" }
        ],
        showFlg: [
          { required: true, message: "宣传展示不能为空", trigger: "change" }
        ],
        enableState: [
          { required: true, message: "停启状态不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getUserList();
    this.getDoctorList();
  },
  methods: {
    // 查询表数据
    getDoctorList() {
      this.selectDoctorOptions = [];
      this.doctorOptions = {};
      listSelectDoctor().then(res => {
        if(res && res.data){}
        res.data.forEach(p =>{
          this.doctorOptions[p.userId] = p.nickName;
          this.selectDoctorOptions.push({ label: (p.nickName + '('  + (p.dept && p.dept.deptName ? p.dept.deptName + '-':'' ) + p.phonenumber + ')'), value: p.userId });
        })
      });
    },
    // 查询表数据
    getUserList() {
      this.allUserListOptions = [];
      listUser({
        pageNum: 1,
        pageSize: 100000,
      }).then(res => {
        if(res && res.rows){}
        res.rows.forEach(p =>{
          this.allUserListOptions.push({ label: (p.nickName + '('  + (p.dept && p.dept.deptName ? p.dept.deptName + '-':'' ) + p.phonenumber + ')'), value: p.userId });
        })
      });
    },
    /** 查询医生管理列表 */
    getList() {
      this.loading = true;
      listDoctor(this.queryParams).then(response => {
        this.doctorList = response.rows;
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
        sysUserId: null,
        title: null,
        introduce: null,
        expertise: null,
        showFlg: null,
        showOrder: null,
        enableState: null,
        createTime: null,
        updateTime: null,
        createBy: null,
        updateBy: null,
        remark: null
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
      this.title = "添加医生管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDoctor(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改医生管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDoctor(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDoctor(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除医生管理编号为"' + ids + '"的数据项？').then(function() {
        return delDoctor(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('doctor/doctor/export', {
        ...this.queryParams
      }, `doctor_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
