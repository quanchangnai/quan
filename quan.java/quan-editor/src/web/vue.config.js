module.exports = {
    publicPath: "/editor",
    outputDir: "../main/resources/static",
    filenameHashing: true,
    configureWebpack: {
        devtool: 'eval-source-map'
    },
    devServer: {
        proxy: 'http://127.0.0.1:9090'
    }

};