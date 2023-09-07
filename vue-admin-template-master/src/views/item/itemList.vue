<template>
  <el-main>
    <el-form
      :model="parms"
      ref="searchForm"
      :rules="rules"
      label-width="80px"
      :inline="true"
      size="normal"
    >
      <el-form-item label="商品名称">
        <el-input v-model="parms.name"></el-input>
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
          <el-form-item prop="name" label="商品名称">
            <el-input v-model="addModel.name"></el-input>
          </el-form-item>
          <el-form-item prop="code" label="商品编号">
            <el-input v-model="addModel.code"></el-input>
          </el-form-item>
          <el-form-item prop="stock" label="库存">
            <el-input v-model="addModel.stock"></el-input>
          </el-form-item>
          <el-form-item prop="purchaseTime" label="采购时间">
            <el-input v-model="addModel.purchaseTime"></el-input>
          </el-form-item>
          <el-form-item prop="isActive" label="是否有效">
            <el-input v-model="addModel.isActive"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>

    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="id" label="id"></el-table-column>
      <el-table-column prop="name" label="商品名称"></el-table-column>
      <el-table-column prop="code" label="商品编码"></el-table-column>
      <el-table-column prop="stock" label="库存"></el-table-column>
      <el-table-column prop="isActive" label="是否上架"></el-table-column>
      <!-- <el-table-column prop="isKill" label="是否秒杀"></el-table-column> -->
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
  </el-main>
</template>

<script>
import SysDialog from "@/components/system/SysDialog.vue";
import {getListApi, editApi, addApi, deleteApi} from "@/api/item.js"

export default {
  components: { SysDialog },
  data() {
    return {
      // 表格的高度
      tableHeight: 0,
      // 表格的数据
      tableList: [],
      // 表单验证规则
      rules: {
        name: [
          {
            trigger: "change",
            message: "请填写商品名称",
            required: true,
          },
        ],
        code: [
          {
            trigger: "change",
            message: "请填写商品编号",
            required: true,
          },
        ],
        stock: [
          {
            trigger: "change",
            message: "请填写商品库存",
            required: true,
          },
        ],
        purchaseTime: [
          {
            trigger: "change",
            message: "请填写商品采购时间",
            required: true,
          },
        ],
        isActive: [
          {
            trigger: "change",
            message: "请选择商品是否上架",
            required: true,
          },
        ],
      },

      // 新增表单绑定的数据域
      addModel: {
        id: "",
        editType: "", // 0: 新增 1: 编辑
        name: "",
        code: "",
        stock: "",
        purchaseTime: "",
        isActive: "",
        isKill: "",
      },

      // 弹框属性
      addDialog: {
        title: "",
        height: 250,
        width: 650,
        visible: false,
      },

      parms: {
        name: "",
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
      let confirm = await this.$myconfirm('确定删除该数据吗？');
      // console.log(confirm);
      if (confirm) {
        let res = await deleteApi({id:row.id});
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
      this.addDialog.title = "编辑用户";
      this.addDialog.visible = true;
      // 数据回显
      this.$objCoppy(row, this.addModel);
      // 设置为编辑
      this.addModel.editType = "1";
    },

    // 搜索按钮
    searchBtn() {
      this.getList();
    },

    // 重置按钮
    resetBtn() {
      this.parms.name = "";
      this.getList();
    },

    // 新增按钮
    addBtn() {
      // 清空表单
      this.$resetForm('addForm', this.addModel);
      this.addModel.editType = "0";
      this.addDialog.title = "新增用户";
      this.addDialog.visible = true;
    },

    // 关闭弹框
    onClose() {
      this.addDialog.visible = false;
    },

    // 确认弹框
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
  },
};
</script>

<style lang="scss" scoped>
</style>