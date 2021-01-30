package io.imtony.vdrive.fxterm.fs

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.io.path.*

@ExperimentalPathApi
class FileSystemExtTests : DescribeSpec() {
  init {
    describe("File System Extension Tests") {
      it("Can read and write custom attributes") {
        val config = Configuration
          .unix()
          .toBuilder()
          .setAttributeViews("basic", "user")
          .setRoots("/")
        val fs = Jimfs.newFileSystem("test", config.build())
        println(fs.rootDirectories)

        val tempDir = fs.getPath("/temp").createDirectory()
        val file = (tempDir / "tempfile.tmp").createFile()

        println("TempDir: $tempDir")
        println("File: $file")
        println("File Attributes:")
        println(file.readAttributes("*").map { "Key: ${it.key} | Value: ${it.value}" }.joinToString("\n"))

        val writeResult = writeUserFileAttribute(file, "user.testattr", "hello")
        writeResult shouldBe true
        val attr = readUserFileAttribute(
          file,
          "user.testattr",
          checkExists = true,
          throwIfNotFound = true
        )
        attr shouldNotBe ""
        attr shouldBe "hello"
      }
    }
  }
}
