<template>
    <div>
        <el-table :data="pageRows" @row-click="onRowClick" size="medium" stripe border :height="height-30">
            <el-table-column v-for="(field,index) in fields"
                             :prop="field.name"
                             :label="field.name"
                             :key="'column-'+index">
                <template v-if="field.showJson"
                          v-slot:default="{row}">
                    {{JSON.stringify(row[field.name])}}
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            style="padding-top: 7px"
            layout="total,prev,pager,next,sizes"
            :page-size="pageSize"
            :page-sizes="[20, 50, 100]"
            :total="rows.length"
            @current-change="onPageChange"
            @size-change="onSizeChange"/>
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
            fields: [],
            rows: [],
            pageSize: 20,
            pageNo: 1,
        };
    },
    async created() {
        let table = await request.post("config/table", FormData.encode({tableName: this.name}));
        this.fields = table.fields;
        this.rows = table.rows;
    },
    computed: {
        pageRows() {
            return this.rows.slice((this.pageNo - 1) * this.pageSize, this.pageNo * this.pageSize);
        }
    },
    methods: {
        onRowClick(row) {
            console.log("onRowClick:" + row)
        },
        onPageChange(page) {
            this.pageNo = page;
        },
        onSizeChange(size) {
            this.pageSize = size;
        }
    }
}
</script>

<style scoped>

</style>