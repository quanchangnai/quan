<template>
    <div id="container">
        <div id="left">
            <el-table :data="showTables" @row-click="onRowClick" size="medium" stripe border height="calc(100% - 10px)">
                <el-table-column prop="tableName">
                    <template #header>
                        <el-input v-model="keyword" clearable size="medium" placeholder="输入关键字" prefix-icon="el-icon-search"/>
                    </template>
                    <template #default="{row}">
                        {{row.tableName}}<br>
                        {{row.configName}}
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div id="right">
            <el-tabs type="border-card" v-model="activeTab" @tab-remove="onTabRemove">
                <el-tab-pane label="测试" name="first">测试测试测试</el-tab-pane>
                <el-tab-pane v-for="(table,index) in selectedTables"
                             :key="'tab-'+index"
                             :name="table"
                             :label="table"
                             closable>
                    <config-table :name="table" :height="configTableHeight"/>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script>

import request from "@/request";
import ConfigTable from "@/components/config/ConfigTable";

export default {
    name: "ConfigEditor",
    components: {ConfigTable},
    data() {
        return {
            keyword: "",
            allTables: [],
            showTables: [],
            selectedTables: [],
            activeTab: "first",
            configTableHeight: 0,
        }
    },
    async created() {
        this.allTables = (await request("/config/tables")).data;
        this.showTables = this.allTables;
        window.addEventListener("resize", this.calcConfigTableHeight);
    },
    mounted() {
        this.calcConfigTableHeight();
    },
    destroyed() {
        window.removeEventListener("resize", this.calcConfigTableHeight);
    },
    watch: {
        keyword: function (value) {
            this.showTables = this.allTables.filter(table => {
                return table.tableName.includes(value) || table.configName.includes(value);
            });
        }
    },
    methods: {
        onRowClick(row) {
            if (!this.selectedTables.includes(row.tableName)) {
                this.selectedTables.push(row.tableName);
            }

            if (this.selectedTables.length >= 10) {
                this.selectedTables.shift();
            }

            this.activeTab = row.tableName;
        },
        onTabRemove(tabName) {
            this.selectedTables = this.selectedTables.filter(table => table !== tabName);
            if (tabName === this.activeTab) {
                this.activeTab = "first";
            }
        },
        calcConfigTableHeight() {
            this.configTableHeight = document.querySelector("#right").offsetHeight - 90;
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