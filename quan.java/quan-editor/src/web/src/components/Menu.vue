<template>
    <el-menu mode="horizontal"
             :default-active="defaultItem"
             background-color="#545c64"
             text-color="#fff"
             active-text-color="#409eff"
             @select="onSelect"
             router>
        <el-menu-item index="/config">配置表</el-menu-item>
        <el-menu-item index="/bbb">菜单项2</el-menu-item>
        <el-menu-item index="/ccc/3?limit=3">菜单项3</el-menu-item>
    </el-menu>
</template>

<script>

export default {
    name: "Menu",
    data() {
        return {
            defaultItem: ""
        }
    },
    created() {
        this.defaultItem = this.$route.fullPath;
    },
    watch: {
        "$route.fullPath": function (value) {
            this.defaultItem = value;
        }
    },
    methods: {
        onSelect(index) {
            // this.$router.push(index)
            console.log("selected:" + index);
        }
    }
}
</script>

<style scoped>

</style>