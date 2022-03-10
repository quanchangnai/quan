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
                          tooltip-effect="light"
                          :data="visibleTables"
                          @row-click="selectConfig">
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
                     v-model="activeTable"
                     @tab-click="onTabClick"
                     @tab-remove="onTabRemove">
                <el-tab-pane v-for="table in selectedTables"
                             :key="'tab-'+table"
                             :name="table"
                             :label="table"
                             closable>
                    <config-table :name="table"
                                  :ref="'tab-'+table"
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
            activeTable: "",
            configTableHeight: 0,
        }
    },
    async created() {
        window.addEventListener("resize", this.calcConfigTableHeight);
        this.allTables = await request.get("/config/tables");
        this.visibleTables = this.allTables;
        if (this.visibleTables.length > 0) {
            this.selectConfig(this.visibleTables[0]);
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
        }
    },
    methods: {
        selectConfig(config) {
            if (!this.selectedTables.includes(config.tableName)) {
                this.selectedTables.push(config.tableName);
            }

            if (this.selectedTables.length >= 10) {
                this.selectedTables.shift();
            }

            this.activeTable = config.tableName;
        },
        onTabClick() {
            // noinspection JSUnresolvedFunction
            this.$refs["tab-" + this.activeTable][0].doLayout();
        },
        onTabRemove(table) {
            this.selectedTables = this.selectedTables.filter(t => t !== table);
            if (table === this.activeTable) {
                this.activeTable = this.selectedTables[0];
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