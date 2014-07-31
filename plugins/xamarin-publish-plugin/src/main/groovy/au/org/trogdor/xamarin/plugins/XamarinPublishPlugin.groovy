package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.lib.XamarinProject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.publish.maven.MavenPublication

class XamarinPublishPlugin implements Plugin<Project> {
	void apply(Project project) {
        project.extensions.create("xamarinPublish", XamarinPublishExtension, project)
        project.plugins.apply('maven-publish')
        ((ProjectInternal)project).getConfigurationActions().add(new Action<ProjectInternal>() {
            @java.lang.Override
            void execute(ProjectInternal projectInternal) {
                XamarinProject xamarinProject = projectInternal.xamarin.xamarinProject
                def resolvedArtifactId = projectInternal.xamarinPublish.artifactId ?: xamarinProject.resolvedProjectName

                MavenPublication publication = projectInternal.publishing.publications.create('xamarin', MavenPublication)
                publication.artifactId = resolvedArtifactId
                xamarinProject.configurations.all {configuration->
                    addArtifacts(configuration, publication, projectInternal)
                }
                projectInternal.tasks.publishToMavenLocal.dependsOn('buildAll')
                projectInternal.tasks.publish.dependsOn('buildAll')
            }

            private void addArtifacts(configuration, publication, projectInternal) {
                def classifierName = configuration.name.toLowerCase()
                configuration.resolvedBuildOutput.with {
                    publication.artifact(it) {
                        extension "dll"
                        classifier classifierName
                    }
                }
            }
        })
    }
}

class XamarinPublishExtension {
    final def Project project
    private def String mArtifactId

    XamarinPublishExtension(Project project) {
        this.project = project
    }

    void artifactId(String artifactId) {
        mArtifactId = artifactId
    }

    String getArtifactId() {
        mArtifactId
    }
}
