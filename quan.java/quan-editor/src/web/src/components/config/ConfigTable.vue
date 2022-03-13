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
                    <el-dropdown-item v-if="allFields.length-fixedFieldsCount>1">
                        <div class="field-dropdown-item" @click.stop>
                            <el-checkbox v-model="checkAllFields"
                                         :indeterminate="indeterminate"
                                         @change="onCheckAllFieldsChange">
                                全选
                            </el-checkbox>
                        </div>
                    </el-dropdown-item>
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
                             :key="'column-'+field.name"
                             #default="{row}">
                <span v-html="cell(row[field.name])"></span>
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
            indeterminate: false,
            checkAllFields: false,
            fixedFieldsCount: 0,
            keyword: "",
            pageSize: 20,
            pageNo: 1,
        };
    },
    async mounted() {
        let table = await request.post("config/table", {name: this.name});
        this.initFields(table.fields);
        this.initRows(table.rows);
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
        initFields(fields) {
            fields.forEach((field, index) => {
                field.checked = true;
                field.fixed = index === 0;
                if (field.fixed) {
                    this.fixedFieldsCount++;
                }
                this.allFields.push(field);

            });
            this.setCheckedFields();
        },
        setCheckedFields() {
            this.checkedFields = [];
            let fixedCount = 0;
            for (let field of this.allFields) {
                if (field.checked) {
                    this.checkedFields.push(field);
                }
                if (field.fixed) {
                    fixedCount++;
                }
            }

            let checkedCount = this.checkedFields.length - fixedCount;
            this.checkAllFields = checkedCount === this.allFields.length - fixedCount;
            this.indeterminate = checkedCount > 0 && checkedCount < this.allFields.length - fixedCount;

            this.doLayout();
        },
        initRows(rows) {
            for (let row of rows) {
                for (let key of Object.keys(row)) {
                    if (typeof row[key] === "object") {
                        row[key] = JSON.stringify(row[key]);
                    }
                }
            }

            this.allRows = rows;
            this.setVisibleRows();
        },
        setVisibleRows() {
            if (this.isMatchKeyword()) {
                this.visibleRows = this.allRows;
                return;
            }

            this.visibleRows = [];

            for (let row of this.allRows) {
                for (let field of this.checkedFields) {
                    if (this.isMatchKeyword(row[field.name])) {
                        this.visibleRows.push(row);
                        break;
                    }
                }
            }

            this.doLayout();
        },
        isMatchKeyword(value) {
            let keyword = this.keyword.trim().toLowerCase();
            if (keyword === "") {
                return -1;
            }
            if (value?.toString().toLowerCase().includes(keyword)) {
                return 1;
            }
            return 0;
        },
        onCheckAllFieldsChange(value) {
            this.indeterminate = false;
            for (let field of this.allFields) {
                if (!field.fixed) {
                    field.checked = value;
                }
            }
            this.setCheckedFields();
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
        cell(value) {
            if (value===undefined){
                return "";
            }
            return value.toString().replace(new RegExp(this.keyword, "ig"), substr=>{
               return `<span style="color: red">${substr}</span>`;
            })
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