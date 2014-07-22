package au.org.trogdor.xamarin.lib

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import groovy.transform.InheritConstructors
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskExecutionException

class XamarinProject implements NamedDomainObjectFactory<XamarinConfiguration>{
	final Project project
    final NamedDomainObjectCollection<XamarinConfiguration> configurationContainer
    protected String mDepDir = "dependencies"
    protected Project mSolutionProject
    private String mProjectFile
    private String mSourceDir
    private String mProjectName
    private Boolean mRestoreNuget

	XamarinProject(Project prj) {
        project = prj
        mSolutionProject = project.rootProject
        configurationContainer = prj.container(XamarinConfiguration, this)
    }

    XamarinConfiguration create(String name) {
        new XamarinConfiguration(name, project, this)
    }

    def configurations(Closure closure) {
        configurationContainer.configure(closure)
    }

    def getConfigurations() {
        configurationContainer
    }

    def dependencyDir(String depDir) {
        mDepDir = depDir
    }

    String getDependencyDir() {
        mDepDir
    }

    def solutionProject(Project solutionProject) {
        mSolutionProject = solutionProject
    }

    def solutionProject(String solutionProjectName) {
        mSolutionProject = project.findProject(solutionProjectName)
    }

    def getSolutionProject() {
        mSolutionProject
    }

    def getSolutionFile() {
        def slnFile = solutionProject.xamarin.solution
        if (!slnFile)
            throw new ProjectConfigurationException("Solution project needs solution file to be set!", null)
        solutionProject.file(slnFile).path
    }

    def getSolutionDir() {
        solutionProject.file(solutionFile).parent + File.separator
    }

    def projectFile(String projectFileName) {
        mProjectFile = projectFileName
    }

    def getProjectFile() {
        if (!mProjectFile)
            throw new ProjectConfigurationException("Project file must be set!", null)
        mProjectFile
    }

    def getProjectDir() {
        project.file(projectFile).parent + File.separator
    }

    def getSourceDir() {
        projectDir
    }

    def projectName(String projectName) {
        mProjectName = projectName
    }

    def getInferredName() {
        def fullName = project.file(projectFile).name
        def inferredName = fullName.lastIndexOf('.').with {it != -1 ? fullName[0..<it] : fullName}
        inferredName
    }

    def getProjectName() {
        mProjectName
    }

    def getResolvedProjectName() {
        if (mProjectName)
            return mProjectName
        else
            return inferredName
    }

    def restoreNuget(Boolean restore) {
        mRestoreNuget = restore
    }

    def getRestoreNuget() {
        mRestoreNuget
    }
}

@InheritConstructors
class XBuildProject extends XamarinProject {
	def buildTask() {
		return XBuildCompileTask
	}

	def cleanTask() {
		return XBuildCleanTask
	}
}

@InheritConstructors
class AndroidLibraryProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new AndroidLibraryConfiguration(name, project, this)
    }
}

@InheritConstructors
class AndroidAppProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new AndroidAppConfiguration(name, project, this)
    }

    def buildTask() {
        return XBuildAndroidPackageTask
    }
}

@InheritConstructors
class MDToolProject extends XamarinProject {
	def buildTask() {
		return MDToolCompileTask
	}

	def cleanTask() {
		return MDToolCleanTask
	}
}

@InheritConstructors
class iOSLibraryProject extends MDToolProject {
    XamarinConfiguration create(String name) {
        return new iOSLibraryConfiguration(name, project, this)
    }
}


@InheritConstructors
class iOSAppProject extends MDToolProject {
    XamarinConfiguration create(String name) {
        return new iOSAppConfiguration(name, project, this)
    }
}

@InheritConstructors
class GenericLibraryProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new GenericLibraryConfiguration(name, project, this)
    }
}

@InheritConstructors
class GenericAppProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new GenericAppConfiguration(name, project, this)
    }
}

@InheritConstructors
class NUnitProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new NUnitConfiguration(name, project, this)
    }
}
