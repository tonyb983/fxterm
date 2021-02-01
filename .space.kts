/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Run Qodana") {
 	container("jetbrains/qodana:2020.3-eap") {
        args("-v ${}:/data/project -v ${GITHUB_WORKSPACE}/qodana:/data/results --save-report")
    }
}
