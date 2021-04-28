<template>
    <div id="container">
        <div id="left">
            <el-table :data="showConfigs" @row-click="onRowClick" size="medium" stripe border height="calc(100% - 10px)">
                <el-table-column prop="name">
                    <template #header>
                        <el-input v-model="keyword" size="medium" placeholder="输入关键字" prefix-icon="el-icon-search"/>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div id="right">
            <el-tabs type="border-card" value="first" @tab-remove="onTabRemove">
                <el-tab-pane label="测试" name="first">测试测试测试</el-tab-pane>
                <el-tab-pane v-for="config in selectedConfigs"
                             :key="config"
                             :label="config"
                             :name="config"
                             closable>
                    {{config}}
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script>
export default {
    name: "ConfigEditor",
    data() {
        const allConfigs = [];
        for (let i = 1; i <= 100; i++) {
            allConfigs.push({name: "config" + i});
        }
        return {
            keyword: "",
            allConfigs,
            showConfigs: allConfigs,
            selectedConfigs: []
        }
    },
    created() {
        console.log("ConfigEditor route.path:" + this.$route.fullPath);
    },
    watch: {
        keyword: function (value) {
            this.showConfigs = this.allConfigs.filter(c => c.name.indexOf(value) >= 0);
        }
    },
    methods: {
        onRowClick(row) {
            if (!this.selectedConfigs.includes(row.name)) {
                this.selectedConfigs.push(row.name);
            }
            if (this.selectedConfigs.length >= 10) {
                this.selectedConfigs.shift();
            }
        },
        onTabRemove(name) {
            this.selectedConfigs = this.selectedConfigs.filter(c => c !== name);
        }
    }
}
</script>

<!--suppress CssUnusedSymbol -->
<style scoped>

#container {
    height: 100%;
}

#left, #right {
    position: absolute;
    height: 100%;
    box-sizing: border-box;
    padding-top: 10px;
}

#left {
    width: 300px;
}

#right {
    left: 310px;
    right: 0;
}

.el-tabs {
    height: calc(100% - 12px)
}

</style>