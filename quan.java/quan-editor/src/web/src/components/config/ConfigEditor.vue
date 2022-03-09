<template>
    <div ref="body" class="config-editor">
        <div id="left">
            <div ref="search" class="search">
                <el-input v-model="keyword"
                          clearable
                          size="small"
                          placeholder="输入关键字搜索"
                          prefix-icon="el-icon-search"/>
            </div>
            <el-scrollbar ref="scrollbar" :style="{height: listHeight}">
                <el-table ref="table"
                          stripe
                          size="medium"
                          :show-header="false"
                          :data="showTables"
                          @row-click="onRowClick">
                    <el-table-column prop="tableName">
                        <template #default="{row}">
                            {{ row.tableName }}<br>
                            {{ row.configName }}
                        </template>
                    </el-table-column>
                </el-table>
            </el-scrollbar>
        </div>
        <div id="right">
            <el-tabs type="border-card"
                     v-model="activeTab"
                     @tab-click="onTabClick"
                     @tab-remove="onTabRemove">
                <el-tab-pane label="测试" name="first">测试测试测试</el-tab-pane>
                <el-tab-pane v-for="table in selectedTables"
                             :key="'tab-'+table"
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
            listHeight: "100%",
            keyword: "",
            allTables: [],
            showTables: [],
            selectedTables: [],
            activeTab: "first",
            configTableHeight: 0,
        }
    },
    async created() {
        window.addEventListener("resize", this.calcConfigTableHeight);
        this.allTables = await request.get("/config/tables");
        this.showTables = this.allTables;
    },
    mounted() {
        this.calcConfigTableHeight();
        this.resizeObserver = new ResizeObserver(this.doLayout);
        this.resizeObserver.observe(this.$refs.body);
    },
    destroyed() {
        window.removeEventListener("resize", this.calcConfigTableHeight);
        this.resizeObserver.disconnect();
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
        onTabClick(tab) {
            // noinspection JSUnresolvedFunction
            this.$refs["tab-" + tab.name][0].doLayout();
        },
        onTabRemove(tabName) {
            this.selectedTables = this.selectedTables.filter(table => table !== tabName);
            if (tabName === this.activeTab) {
                this.activeTab = "first";
            }
        },
        calcConfigTableHeight() {
            this.configTableHeight = document.querySelector("#right").offsetHeight - 90;
        },
        async doLayout() {
            this.listHeight = (this.$refs.body.offsetHeight - this.$refs.search.offsetHeight - 21) + "px";
            await this.$nextTick();
            this.$refs.scrollbar.update();
            this.$refs.table.doLayout();
        }
    }
}
</script>

<!--suppress CssUnusedSymbol -->
<style scoped>

.config-editor {
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

.search {
    position: relative;
    margin-left: 8px;
    margin-bottom: -1px;
    padding: 8px 10px;
    border: solid #ebeef5 1px;
    z-index: 10;
}

.el-scrollbar {
    border: solid #ebeef5 1px;
    left: 8px;
    width: calc(100% - 10px);
}

.el-scrollbar >>> .el-scrollbar__wrap {
    overflow-x: hidden;
}

.el-table:before {
    content: none;
}

>>> .el-table__empty-block {
    margin-top: 40vh;
}

.el-tabs {
    height: calc(100% - 12px);
    width: calc(100% - 10px);
}

</style>