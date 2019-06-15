package com.xtc.gradleplugin

import org.gradle.api.Plugin
import org.gradle.api.Project


class HelloPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        /**
         * 生成closure
         * apkdistconf{
         *     nameMap = ‘’
         *     destDir = ‘’
         * }
         */
        project.extensions.create('apkdistconf', ApkDistExtension)
        project.task('hellotask'){
            doFirst{
                println 'hello, this is test task!'
            }
        }
        // 所有模块都配置完了之后执行
        project.afterEvaluate {
            if (!project.android){
                throw new IllegalStateException("Must apply 'com.android.application' or 'com.android.library' first!")
            }

            if (project.apkdistconf.nameMap == null || project.apkdistconf.destDir == null){
                project.logger.info('Apkdist conf should be set!')
                return
            }

            Closure nameMap = project['apkdistconf'].nameMap
            String destDir = project['apkdistconf'].destDir

            // 枚举每一个build variant,生成的apk 放入如下路径和文件名
            project.android.applicationVariants.all {
                variant.outputs.each { output ->
                    File file = output.outputFile
                    println("dir " + destDir + " " + file.getName())
                    output.outputFile = new File(destDir, nameMap(file.getName()))
                }
            }
        }

    }
}