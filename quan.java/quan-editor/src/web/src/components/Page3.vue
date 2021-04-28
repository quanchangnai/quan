<template>
    <div>ccc</div>
</template>

<script>
export default {
    name: "Page3",
    props: {
        type: String
    },
    created() {
        console.log("route.path:" + this.$route.path);
        console.log("route.query:" + JSON.stringify(this.$route.query));
        console.log("route.params:%s", JSON.stringify(this.$route.params));
        console.log("route.params.type:" + this.type);

    }
}
</script>

<style scoped>

</style>