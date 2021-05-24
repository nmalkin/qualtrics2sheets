package q2s.qualtrics

class QualtricsURL(private val datacenter: QualtricsDatacenter) {
    val GET_TOKEN_URL = "https://$datacenter.qualtrics.com/oauth2/token"
    val WHOAMI_URL = "https://$datacenter.qualtrics.com/API/v3/whoami"

    /**
     * https://api.qualtrics.com/guides/reference/responseImportsExports.json/paths/~1surveys~1%7BsurveyId%7D~1export-responses/post
     */
    fun startExport(surveyID: String) = "https://$datacenter.qualtrics.com/API/v3/surveys/$surveyID/export-responses"

    /**
     * https://api.qualtrics.com/guides/reference/responseImportsExports.json/paths/~1surveys~1%7BsurveyId%7D~1export-responses~1%7BexportProgressId%7D/get
     */
    fun getExportProgress(surveyID: String, progressID: String) =
        "https://$datacenter.qualtrics.com/API/v3/surveys/$surveyID/export-responses/$progressID"

    /**
     * https://api.qualtrics.com/guides/reference/responseImportsExports.json/paths/~1surveys~1%7BsurveyId%7D~1export-responses~1%7BfileId%7D~1file/get
     */
    fun getExportFile(surveyID: String, fileID: String) =
        "https://$datacenter.qualtrics.com/API/v3/surveys/$surveyID/export-responses/$fileID/file"
}
