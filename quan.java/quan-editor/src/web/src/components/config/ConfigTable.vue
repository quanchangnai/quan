<template>
    <div class="config-table">
        <div class="tool-bar">
            <el-dropdown class="tool-item"
                         trigger="click"
                         placement="bottom-start">
                <el-button plain size="small">
                    筛选字段
                    <i class="el-icon-arrow-down el-icon--right"/>
                </el-button>
                <el-dropdown-menu slot="dropdown" :class="{'too-much-item-dropdown-menu':allFields.length>15}">
                    <el-dropdown-item v-for="field in allFields"
                                      :key="field.name">
                        <div class="field-dropdown-item" @click.stop>
                            <el-checkbox v-model="field.checked"
                                         :disabled="field.fixed"
                                         @change="onFieldCheckedChange">
                                {{ field.name }}
                            </el-checkbox>
                        </div>
                    </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
            <el-input v-model="keyword"
                      clearable
                      size="small"
                      class="tool-item"
                      style="width: 200px;"
                      placeholder="输入关键字搜索"
                      prefix-icon="el-icon-search"/>
            <el-pagination class="tool-item"
                           :page-size="pageSize"
                           :page-sizes="[50, 100,200,500]"
                           :total="visibleRows.length"
                           layout="total,prev,pager,next,sizes"
                           @current-change="onPageChange"
                           @size-change="onSizeChange"/>
        </div>
        <el-table ref="table"
                  :data="pageRows"
                  stripe border
                  size="medium"
                  :height="height-45"
                  @row-click="onRowClick">
            <el-table-column v-for="field in checkedFields"
                             sortable
                             :prop="field.name"
                             :label="field.name"
                             min-width="150px"
                             :fixed="field.fixed"
                             :key="'column-'+field.name">
                <template v-if="field.showJson"
                          v-slot:default="{row}">
                    {{ JSON.stringify(row[field.name]) }}
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import request from "@/request";

export default {
    name: "ConfigTable",
    props: {
        name: String,
        height: Number
    },
    data() {
        return {
            allFields: [],
            checkedFields: [],
            allRows: [],
            visibleRows: [],
            keyword: "",
            pageSize: 20,
            pageNo: 1,
        };
    },
    async mounted() {
        let table = await request.post("config/table", FormData.encode({tableName: this.name}));
        table.fields.forEach((field, index) => {
            field.checked = true;
            field.fixed = index === 0;
            this.allFields.push(field)
        });
        this.setCheckedFields();

        for (let row of table.rows) {
            for (let key of Object.keys(row)) {
                if (Array.isArray(row[key])) {
                    row[key] = JSON.stringify(row[key]);
                }
            }
        }
        this.allRows = table.rows;
        this.setVisibleRows();
    },
    computed: {
        pageRows() {
            return this.visibleRows.slice((this.pageNo - 1) * this.pageSize, this.pageNo * this.pageSize);
        }
    },
    watch: {
        keyword() {
            this.setVisibleRows();
        },
    },
    methods: {
        setCheckedFields() {
            this.checkedFields = this.allFields.filter(field => field.checked);
            this.doLayout();
        },
        setVisibleRows() {
            let keyword = this.keyword.trim().toLowerCase();
            this.visibleRows = keyword === "" ? this.allRows : [];
            if (keyword === "") {
                return;
            }

            for (let row of this.allRows) {
                for (let field of this.checkedFields) {
                    if (row[field.name]?.toString().toLowerCase().includes(keyword)) {
                        this.visibleRows.push(row);
                        break;
                    }
                }
            }
        },
        onFieldCheckedChange() {
            this.setCheckedFields();
            this.setVisibleRows();
        },
        onRowClick(row) {
            console.log("onRowClick:" + row)
        },
        onPageChange(page) {
            this.pageNo = page;
        },
        onSizeChange(size) {
            this.pageSize = size;
        },
        async doLayout() {
            await this.$nextTick();
            this.$refs.table.doLayout();
        }
    }
}
</script>

<style scoped>
.tool-bar {
    width: 100%;
    padding-bottom: 11px;
}

.tool-item {
    display: inline-block;
    margin-right: 20px;
}

.el-dropdown-menu {
    transform: translateY(-7px);
}

.too-much-item-dropdown-menu {
    max-height: 61vh;
    overflow-y: auto;
}

.el-dropdown-menu__item {
    padding: 0;
}

.field-dropdown-item {
    padding: 0 20px;
}

.el-pagination {
    text-align: right;
    position: absolute;
    right: 0;
}
</style>