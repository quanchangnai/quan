<template>
    <div ref="body" class="config-list">
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
                          highlight-current-row
                          tooltip-effect="light"
                          :data="visibleTables"
                          @current-change="selectTable">
                    <el-table-column :show-overflow-tooltip="true">
                        <template #default="{row}">
                            {{ row.tableName }}<br>
                            {{ row.configName }}
                        </template>
                    </el-table-column>
                </el-table>
            </el-scrollbar>
        </div>
        <div id="right">
            <el-tabs ref="tabs"
                     type="border-card"
                     v-model="activeTableName"
                     @tab-click="onTabClick"
                     @tab-remove="onTabRemove">
                <el-tab-pane v-for="table in selectedTables"
                             :key="'tab-'+table.tableName"
                             :name="table.tableName"
                             :label="table.tableName"
                             closable>
                    <config-table :name="table.tableName"
                                  :ref="'config-table-'+table.tableName"
                                  :height="configTableHeight"/>
                </el-tab-pane>
                <el-empty v-if="!selectedTables.length" :style="{height: configTableHeight+'px'}"/>
            </el-tabs>
        </div>
    </div>
</template>

<script>

import request from "@/request";
import ConfigTable from "@/components/config/ConfigTable";

export default {
    name: "ConfigList",
    components: {ConfigTable},
    data() {
        return {
            listHeight: "100%",
            keyword: "",
            allTables: [],
            visibleTables: [],
            selectedTables: [],
            activeTableName: "",
            configTableHeight: 0,
        }
    },
    async created() {
        window.addEventListener("resize", this.calcConfigTableHeight);
        this.allTables = await request.get("/config/tables");
        this.visibleTables = this.allTables;
        if (this.visibleTables.length > 0) {
            this.selectTable(this.visibleTables[0]);
        }
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
            this.visibleTables = this.allTables.filter(table => {
                return table.tableName.includes(value) || table.configName.includes(value);
            });

            let keyword = this.keyword.trim().toLowerCase();
            let prefix = false;
            if (keyword.startsWith("#")) {
                prefix = true;
                keyword = keyword.substring(1);
            }

            let activeTable;
            this.visibleTables = [];

            for (let table of this.allTables) {
                let visible;
                if (prefix) {
                    visible = table.configName.toLowerCase().startsWith(keyword) || table.tableName.toLowerCase().startsWith(keyword);
                } else {
                    visible = table.configName.toLowerCase().includes(keyword) || table.tableName.toLowerCase().includes(keyword);
                }
                if (visible) {
                    this.visibleTables.push(table);
                    if (table.tableName === this.activeTableName) {
                        activeTable = table;
                    }
                }
            }

            if (activeTable) {
                this.$nextTick(() => this.$refs.table.setCurrentRow(activeTable));
            }
        }
    },
    methods: {
        selectTable(table) {
            if (!table) {
                return;
            }
            if (!this.selectedTables.includes(table)) {
                this.selectedTables.push(table);
            }

            if (this.selectedTables.length >= 10) {
                this.selectedTables.shift();
            }

            this.activeTableName = table.tableName;
            this.doConfigTableLayout();
        },
        doConfigTableLayout() {
            this.$nextTick(() => {
                // noinspection JSUnresolvedFunction
                this.$refs["config-table-" + this.activeTableName][0].doLayout();
            });
        },
        onTabClick(tab) {
            this.$refs.table.setCurrentRow(this.selectedTables[tab.index]);
            this.doConfigTableLayout();
        },
        onTabRemove(table) {
            this.selectedTables = this.selectedTables.filter(t => t !== table);
            if (table === this.activeTableName) {
                this.activeTableName = this.selectedTables[0];
            }
        },
        calcConfigTableHeight() {
            // noinspection JSUnresolvedVariable
            this.configTableHeight = this.$refs.tabs.$el.offsetHeight - 60;
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
.config-list {
    height: 100%;
}

#left, #right {
    position: absolute;
    height: 100%;
    box-sizing: border-box;
    padding-top: 10px;
}

#left {
    width: 250px;
}

#right {
    left: 260px;
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

>>> #left .el-table__empty-block {
    margin-top: 50%;
}

.el-tabs {
    height: calc(100% - 12px);
    width: calc(100% - 10px);
}

>>> .el-tabs__content {
    padding-top: 10px;
}
</style>