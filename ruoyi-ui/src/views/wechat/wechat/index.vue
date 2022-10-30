<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="创建者" prop="createId">
        <el-input
          v-model="queryParams.createId"
          placeholder="请输入创建者"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="更新者" prop="updateId">
        <el-input
          v-model="queryParams.updateId"
          placeholder="请输入更新者"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户备注" prop="remark">
        <el-input
          v-model="queryParams.remark"
          placeholder="请输入用户备注"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="昵称" prop="nickName">
        <el-input
          v-model="queryParams.nickName"
          placeholder="请输入昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所在城市" prop="city">
        <el-input
          v-model="queryParams.city"
          placeholder="请输入所在城市"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所在国家" prop="country">
        <el-input
          v-model="queryParams.country"
          placeholder="请输入所在国家"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所在省份" prop="province">
        <el-input
          v-model="queryParams.province"
          placeholder="请输入所在省份"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户语言" prop="language">
        <el-input
          v-model="queryParams.language"
          placeholder="请输入用户语言"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户组" prop="groupId">
        <el-input
          v-model="queryParams.groupId"
          placeholder="请输入用户组"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="标签列表" prop="tagidList">
        <el-input
          v-model="queryParams.tagidList"
          placeholder="请输入标签列表"
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
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['wechat:wechat:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['wechat:wechat:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['wechat:wechat:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['wechat:wechat:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="wechatList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="更新者" align="center" prop="updateId" />
      <el-table-column label="用户备注" align="center" prop="remark" />
      <el-table-column label="昵称" align="center" prop="nickName" />
      <el-table-column label="性别" align="center" prop="sex" />
      <el-table-column label="所在城市" align="center" prop="city" />
      <el-table-column label="所在国家" align="center" prop="country" />
      <el-table-column label="所在省份" align="center" prop="province" />
      <el-table-column label="手机号码" align="center" prop="phone" />
      <el-table-column label="用户语言" align="center" prop="language" />
      <el-table-column label="头像" align="center" prop="headimgUrl">
        <template #default="scope">
          <img :src="scope.row.headimgUrl" title="点击上传头像" class="img-circle" style="width: 20px;height: 20px"/>
        </template>
      </el-table-column>
      <el-table-column label="用户组" align="center" prop="groupId" />
      <el-table-column label="标签列表" align="center" prop="tagidList" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['wechat:wechat:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['wechat:wechat:remove']"
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

    <!-- 添加或修改微信用户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listWechat, getWechat, delWechat, addWechat, updateWechat } from "@/api/wechat/wechat";

export default {
  name: "Wechat",
  data() {
    return {
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
      // 微信用户表格数据
      wechatList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        createId: null,
        updateId: null,
        remark: null,
        nickName: null,
        sex: null,
        city: null,
        country: null,
        province: null,
        phone: null,
        language: null,
        headimgUrl: null,
        groupId: null,
        tagidList: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询微信用户列表 */
    getList() {
      this.loading = true;
      listWechat(this.queryParams).then(response => {
        this.wechatList = response.rows;
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
        createId: null,
        createTime: null,
        updateId: null,
        updateTime: null,
        remark: null,
        delFlag: null,
        appType: null,
        subscribe: null,
        subscribeScene: null,
        subscribeTime: null,
        subscribeNum: null,
        cancelSubscribeTime: null,
        openId: null,
        nickName: null,
        sex: null,
        city: null,
        country: null,
        province: null,
        phone: null,
        language: null,
        headimgUrl: null,
        unionId: null,
        groupId: null,
        tagidList: null,
        qrSceneStr: null,
        latitude: null,
        longitude: null,
        precision: null,
        sessionKey: null
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
      this.title = "添加微信用户";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getWechat(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改微信用户";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateWechat(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addWechat(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除微信用户编号为"' + ids + '"的数据项？').then(function() {
        return delWechat(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('wechat/wechat/export', {
        ...this.queryParams
      }, `wechat_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
