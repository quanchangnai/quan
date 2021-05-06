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
            <el-tabs type="border-card" v-model="activeTab" @tab-remove="onTabRemove">
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

import request from "@/request";

export default {
    name: "ConfigEditor",
    data() {
        return {
            keyword: "",
            allConfigs: [],
            showConfigs: [],
            selectedConfigs: [],
            activeTab: "first"
        }
    },
    async created() {
        this.allConfigs = (await request("/config/list")).data;
        this.showConfigs = this.allConfigs;
    },
    watch: {
        keyword: function (value) {
            this.showConfigs = this.allConfigs.filter(c => c.name.includes(value));
        }
    },
    methods: {
        onRowClick(row) {
            if (!this.selectedConfigs.includes(row.name)) {
                this.selectedConfigs.push(row.name);
                this.activeTab = row.name;
            }
            if (this.selectedConfigs.length >= 10) {
                this.selectedConfigs.shift();
            }
        },
        onTabRemove(name) {
            this.selectedConfigs = this.selectedConfigs.filter(c => c !== name);
            if (name === this.activeTab) {
                this.activeTab = "first";
            }
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