package au.org.trogdor.xamarin.lib

import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class MDToolTask extends DefaultTask {
	XamarinProject xamarinProject
	protected def solutionFilePath

	def generateCommand() {
		return []
	}

	@TaskAction
	def build() {
		solutionFilePath = project.file(xamarinProject.solutionFile).path
		def proc = generateCommand().execute()
		def serr = new ByteArrayOutputStream(4096)
		proc.waitForProcessOutput(System.out, serr)

		if(proc.exitValue())
			throw new TaskExecutionException(this, null)
	}
}

class MDToolCompileTask extends MDToolTask {
    XamarinConfiguration configuration

	def generateCommand() {
		[project.xamarin.mdtoolPath, 'build', '-t:Build', "-p:${xamarinProject.projectName}", "-c:${configuration.name}|iPhone", solutionFilePath]
	}
}

class MDToolCleanTask extends MDToolTask {
    @TaskAction
    def build() {
        def serr = new ByteArrayOutputStream(4096)

        solutionFilePath = project.file(xamarinProject.solutionFile).path
        xamarinProject.configurationContainer.all() { configuration ->
            def proc = generateCommand(configuration).execute()
            proc.waitForProcessOutput(System.out, serr)

            if (proc.exitValue())
                throw new TaskExecutionException(this, null)
        }
    }

	def generateCommand(XamarinConfiguration configuration) {
		[project.xamarin.mdtoolPath, 'build', '-t:Clean', "-p:${xamarinProject.projectName}", "-c:${configuration.name}|iPhone", solutionFilePath]
	}
}