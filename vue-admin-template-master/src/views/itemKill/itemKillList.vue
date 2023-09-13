<template>
  <el-main>
    <el-form
      :model="parms"
      ref="searchForm"
      label-width="80px"
      :inline="true"
      size="normal"
    >
      <el-form-item label="商品名称">
        <el-input v-model="parms.itemName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">搜索</el-button>
        <el-button
          icon="el-icon-refresh-left"
          @click="resetBtn"
          style="color: #ff7670"
          >重置</el-button
        >
        <el-button
          type="primary"
          @click="addBtn"
          icon="el-icon-circle-plus-outline"
          >新增</el-button
        >
      </el-form-item>
    </el-form>

    <!-- 用户表格
        :data : 表格的数据
        el-table-column 中的 prop需要与返回的字段对应
        lable 字段的名称（自定义
      -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="id" label="编号" width="60"></el-table-column>
      <el-table-column prop="itemName" label="商品名称"></el-table-column>
      <el-table-column
        prop="nowPrice"
        label="秒杀价"
        width="80"
      ></el-table-column>
      <el-table-column
        prop="beforePrice"
        label="原价"
        width="80"
      ></el-table-column>
      <el-table-column
        prop="total"
        label="秒杀总数"
        width="100"
      ></el-table-column>
      <el-table-column prop="startTime" label="秒杀开始时间"></el-table-column>
      <el-table-column prop="endTime" label="秒杀结束时间"></el-table-column>
      <el-table-column
        prop="isActive"
        label="是否开启秒杀"
        width="110"
      ></el-table-column>
      <el-table-column align="center" width="180" label="操作">
        <template slot-scope="scope">
          <el-button
            icon="el-icon-edit"
            type="primary"
            size="small"
            @click="editBtn(scope.row)"
            >编辑</el-button
          >
          <el-button
            icon="el-icon-delete"
            type="danger"
            size="small"
            @click="deleteBtn(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页
        @size-change: 页容量改变时触发的事件
        @current-change: 页数改变时触发的事件
        :current-page: 当前第几页
        :page-sizes: 
        :page-size: 每页查询几条
        :total: 总条数
      -->
    <el-pagination
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page.sync="parms.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]"
      :page-size="parms.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="parms.total"
      background
    >
      :pager-count="7">
    </el-pagination>

    <!-- 新增弹框 -->
    <sys-dialog
      :title="addDialog.title"
      :height="addDialog.height"
      :width="addDialog.width"
      :visible="addDialog.visible"
      @onClose="onClose"
      @onConfirm="onConfirm"
    >
      <template slot="content">
        <el-form
          :model="addModel"
          ref="addForm"
          :rules="rules"
          label-width="80px"
          :inline="true"
          size="small"
        >
          <el-form-item prop="itemName" label="商品名称">
            <el-input
              v-model="addModel.itemName"
              v-if="this.addModel.editType === '1'"
              :disabled="true"
            ></el-input>
            <el-input
              v-model="addModel.itemName"
              v-if="this.addModel.editType === '0'"
            ></el-input>
          </el-form-item>
          <el-form-item prop="total" label="秒杀总数">
            <el-input v-model="addModel.total"></el-input>
          </el-form-item>
          <el-form-item prop="startTime" label="开始时间">
            <!-- <el-input v-model="addModel.startTime"></el-input> -->
            <el-date-picker
              style="width: 190px"
              v-model="addModel.startTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              format="yyyy-MM-dd HH:mm:ss"
              placeholder="选择日期时间"
              editable="false"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item prop="endTime" label="结束时间">
            <!-- <el-input v-model="addModel.endTime"></el-input> -->
            <el-date-picker
              style="width: 190px"
              v-model="addModel.endTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              format="yyyy-MM-dd HH:mm:ss"
              placeholder="选择日期时间"
              editable="false"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item prop="isActive" label="是否秒杀">
            <el-input v-model="addModel.isActive"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>
  
  <script>
import SysDialog from "@/components/system/SysDialog.vue";
import { addApi, getListApi, editApi, deleteApi } from "@/api/itemKill";

export default {
  components: { SysDialog },
  data() {
    return {
      // 表格的高度
      tableHeight: 0,
      // 表格的数据
      tableList: [],
      // 新增表单的验证规则
      rules: {
        total: [
          {
            trigger: "change",
            message: "请填写秒杀总数",
            required: true,
          },
        ],
        startTime: [
          {
            trigger: "change",
            message: "请填写秒杀开始时间",
            required: true,
          },
        ],
        endTime: [
          {
            trigger: "change",
            message: "请填写秒杀结束时间",
            required: true,
          },
        ],
        isActive: [
          {
            trigger: "change",
            message: "请选择是否开启秒杀",
            required: true,
          },
        ],
      },
      // 新增表单绑定的数据域
      addModel: {
        id: "",
        editType: "", // 0: 新增 1: 编辑
        itemId: "",
        total: "",
        startTime: "",
        endTime: "",
        isActive: "",
        createTime: "",
        itemName: "",
        canKill: "",
        nowPrice: "",
        beforePrice: "",
      },
      // 弹框属性
      addDialog: {
        title: "",
        height: 160,
        width: 650,
        visible: false,
      },
      parms: {
        itemName: "",
        currentPage: 1, // 从第几页开始
        pageSize: 10, // 每页查询的条数
        total: 0, // 总条数
      },
    };
  },
  created() {
    this.getList();
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 200;
    });
  },
  methods: {
    // 页数改变时触发
    currentChange(val) {
      // console.log(val);
      this.parms.currentPage = val;
      // 重新获取列表
      this.getList();
    },
    // 页容量改变时触发
    sizeChange(val) {
      // console.log(val);
      this.parms.pageSize = val;
      this.parms.currentPage = 1;
      this.getList();
    },
    // 删除按钮
    async deleteBtn(row) {
      console.log(row);
      // 信息确认提示
      let confirm = await this.$myconfirm("确定删除该数据吗？");
      // console.log(confirm);
      if (confirm) {
        let res = await deleteApi({ itemId: row.itemId });
        if (res && res.code === 200) {
          // 信息提示
          this.$message(res.msg);
          // 刷新表格
          this.getList();
        }
      }
    },
    // 编辑按钮
    editBtn(row) {
      console.log(row);
      // 设置弹框属性
      this.addDialog.title = "编辑秒杀商品";
      this.addDialog.visible = true;
      // 数据回显
      this.$objCoppy(row, this.addModel);
      // 设置为编辑
      this.addModel.editType = "1";
    },
    // 获取表格列表
    async getList() {
      let res = await getListApi(this.parms);
      console.log(res);
      if (res && res.code === 200) {
        // 给表格数据赋值
        this.tableList = res.data.list;
        // 总条数
        this.parms.total = res.data.total;
      }
    },
    //搜索按钮
    searchBtn() {
      this.getList();
    },
    // 重置按钮
    resetBtn() {
      this.parms.itemName = "";
      this.getList();
    },
    // 新增按钮
    addBtn() {
      // 清空表单
      this.$resetForm("addForm", this.addModel);
      this.addModel.editType = "0";
      this.addDialog.title = "新增秒杀商品";
      this.addDialog.visible = true;
    },
    onClose() {
      this.addDialog.visible = false;
    },
    onConfirm() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = null;
          if (this.addModel.editType === "0") {
            res = await addApi(this.addModel);
          } else {
            res = await editApi(this.addModel);
          }
          console.log(res);
          if (res && res.code === 200) {
            // 信息提示
            this.$message.success(res.msg);
            // 刷新列表
            this.getList();
            this.addDialog.visible = false;
          }
        }
      });
    },
  },
};
</script>
  
  