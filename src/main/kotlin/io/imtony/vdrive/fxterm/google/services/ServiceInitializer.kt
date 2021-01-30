package io.imtony.vdrive.fxterm.google.services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.docs.v1.Docs
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import io.imtony.vdrive.fxterm.ApplicationScopes
import io.imtony.vdrive.fxterm.Const
import io.imtony.vdrive.fxterm.Resources
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * Convenience wrapper for [GsonFactory.getDefaultInstance]
 */
fun createGsonFactory(): GsonFactory = GsonFactory.getDefaultInstance()

/**
 * Convenience wrapper for [GoogleNetHttpTransport.newTrustedTransport]
 */
fun createHttpTransport(): NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

/**
 * ### Creates the [Credential] object for this app to access google's service apis.
 *
 * @param[httpTrans] The [HttpTransport] implementation to be used for the [GoogleAuthorizationCodeFlow.Builder].
 * @param[gsonFac] The [GsonFactory] implementation to be used in [GoogleClientSecrets.load] and [GoogleAuthorizationCodeFlow.Builder]
 * @return[Credential] The application credential object.
 */
fun createCredentials(
  httpTrans: HttpTransport,
  gsonFac: GsonFactory,
  applicationScopes: ApplicationScopes,
  credInputFile: String,
  tokenOutputDir: String,
  localPort: Int,
): Credential {
  val inStream = Unit::class.java.getResourceAsStream(credInputFile)
    ?: throw FileNotFoundException("Resource not found: $credInputFile")

  val clientSecrets = GoogleClientSecrets.load(gsonFac, InputStreamReader(inStream))

  val flow = GoogleAuthorizationCodeFlow.Builder(httpTrans, gsonFac, clientSecrets, applicationScopes.scopes)
    .setDataStoreFactory(FileDataStoreFactory(File(tokenOutputDir)))
    .setAccessType("offline")
    .build()

  val receiver = LocalServerReceiver.Builder().setPort(localPort).build()
  return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
}

fun createServiceInitializer(
  httpTransport: HttpTransport,
  gsonFactory: GsonFactory,
  credentials: Credential,
  appName: String,
): ServiceInitializer = ServiceInitializerImpl(httpTransport, gsonFactory, credentials, appName)

interface ServiceInitializer {
  /**
   * ### Create the Google [Drive] service.
   *
   * @return[Drive] The newly created sheets service.
   */
  fun createDrive(): Drive

  /**
   * ### Create the [com.google.api.services.docs.v1.Docs] service.
   *
   * @return[Docs] The newly created [Docs] service.
   */
  fun createDocs(): Docs

  /**
   * ### Create the Google [Sheets] service.
   *
   * @return[Sheets] The newly created sheets service.
   */
  fun createSheets(): Sheets
  fun createCalendar(): Calendar

  companion object {
    fun createDefault(): ServiceInitializer {
      val http = createHttpTransport()
      val gson = createGsonFactory()
      val creds = createCredentials(
        http,
        gson,
        Const.GoogleScopes.Instance,
        credInputFile = Resources.Creds.Credentials.Path,
        tokenOutputDir = Const.TokenDirectory.Value,
        Const.DefaultPort.Value
      )

      return ServiceInitializerImpl(
        http,
        gson,
        creds,
        Const.ApplicationName.Value
      )
    }
  }
}

private class ServiceInitializerImpl(
  private val httpTransport: HttpTransport,
  private val gsonFactory: GsonFactory,
  private val credentials: Credential,
  private val appName: String,
) : ServiceInitializer {
  /**
   * ### Create the Google [Drive] service.
   *
   * @return[Drive] The newly created sheets service.
   */
  override fun createDrive(): Drive = Drive.Builder(httpTransport, gsonFactory, credentials)
    .setApplicationName(appName)
    .build()

  /**
   * ### Create the [com.google.api.services.docs.v1].[Docs] service.
   *
   * @return[Docs] The newly created [Docs] service.
   */
  override fun createDocs(): Docs = Docs.Builder(httpTransport, gsonFactory, credentials)
    .setApplicationName(appName)
    .build()

  /**
   * ### Create the Google [Sheets] service.
   *
   * @return[Sheets] The newly created sheets service.
   */
  override fun createSheets(): Sheets = Sheets.Builder(httpTransport, gsonFactory, credentials)
    .setApplicationName(appName)
    .build()

  /**
   * ### Create the Google [Calendar] service.
   */
  override fun createCalendar(): Calendar = Calendar.Builder(httpTransport, gsonFactory, credentials)
    .setApplicationName(appName)
    .build()

}
