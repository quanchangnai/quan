<template>
    <div>
        <div id="header">
            <Menu mode="horizontal"/>
        </div>
        <div id="main">
            <router-view/>
        </div>
    </div>
</template>

<script>

import Menu from "@/components/Menu";

export default {
    name: 'App',
    components: {Menu},
}
</script>

<style scoped>
#header {
    position: absolute;
    width: 100%;
}

#main {
    position: absolute;
    width: 100%;
    top: 60px;
    bottom: 0;
}
</style>
