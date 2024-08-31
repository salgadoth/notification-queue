module.exports = {
    apps: [
        {
            name: "notification-application",
            script: "java",
            args: "-jar ./target/notification-application-1.0-SNAPSHOT.jar",
            watch: false,
            env: {
                NODE_ENV: "production",
                JAVA_OPTS: "-Xms512m -Xmx1024m"
            }
        }
    ]
}