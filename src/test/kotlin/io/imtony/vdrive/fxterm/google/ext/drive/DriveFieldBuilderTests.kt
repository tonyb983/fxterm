package io.imtony.vdrive.fxterm.google.ext.drive

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DriveFieldBuilderTests : DescribeSpec({
  describe("DriveFieldBuilder Tests") {
    it("Should handle simple field requests.") {
      var dfb = DriveFilesListRequestFieldsBuilder().apply {
        files {
          id
          name
          mimeType
        }
      }

      println("1: " + dfb.finalValue)
      dfb.finalValue shouldBe "files(id,name,mimeType)"

      dfb = DriveFilesListRequestFieldsBuilder().apply {
        nextPageToken
        maxResults(10)
        files { fileSystemDefaults() }
      }
      println("2: " + dfb.finalValue)
      dfb.finalValue shouldBe "nextPageToken,maxResults=10,files(id,name,mimeType,starred,trashed,parents,webContentLink,webViewLink,hasThumbnail,thumbnailLink,properties,appProperties,createdTime,modifiedTime,shortcutDetails/*,originalFilename,fullFileExtension,fileExtension)"
    }
  }
})
