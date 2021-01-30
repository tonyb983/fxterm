package io.imtony.vdrive.fxterm.google.ext.drive

import io.imtony.vdrive.fxterm.google.services.GoogleDriveService
import io.imtony.vdrive.fxterm.google.services.ServiceInitializer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe

class DriveQueryExtTests : DescribeSpec({
  describe("Google Query Builder Tests") {
    it("Can create a single query") {
      val q1 = GoogleDriveQueryBuilder().apply {
        isRoot()
      }.getQuery()

      q1 shouldBe "'root' in parents"

      GoogleDriveQueryBuilder().apply {
        notTrashed()
      }.getQuery() shouldBe "trashed = false"

      GoogleDriveQueryBuilder().apply {
        isStarred()
      }.getQuery() shouldBe "starred = true"

      GoogleDriveQueryBuilder().apply {
        term("hello") greaterThan 10
      }.getQuery() shouldBe "hello > '10'"

      GoogleDriveQueryBuilder().apply {
        term("hello") greaterThanOrEqual 10
      }.getQuery() shouldBe "hello >= '10'"

      GoogleDriveQueryBuilder().apply {
        term("hello") lessThan 10
      }.getQuery() shouldBe "hello < '10'"

      GoogleDriveQueryBuilder().apply {
        term("hello") lessThanOrEqual 10
      }.getQuery() shouldBe "hello <= '10'"
    }

    it("Uselessly groups single queries") {
      GoogleDriveQueryBuilder().apply {
        grouped {
          isRoot()
          notTrashed()
        }
      }.getQuery() shouldBe "(trashed = false)"
    }

    it("Ands simple queries") {
      GoogleDriveQueryBuilder().apply {
        and {
          isRoot()
          notTrashed()
        }
      }.getQuery() shouldBe "'root' in parents and trashed = false"

      GoogleDriveQueryBuilder().apply {
        and {
          isRoot()
          notTrashed()
          isStarred()
        }
      }.getQuery() shouldBe "'root' in parents and trashed = false and starred = true"
    }

    it("Ors simple queries") {
      GoogleDriveQueryBuilder().apply {
        or {
          isRoot()
          notTrashed()
        }
      }.getQuery() shouldBe "'root' in parents or trashed = false"

      GoogleDriveQueryBuilder().apply {
        or {
          isRoot()
          notTrashed()
          isStarred()
        }
      }.getQuery() shouldBe "'root' in parents or trashed = false or starred = true"
    }

    it("Can create complex / nested queries") {
      GoogleDriveQueryBuilder().apply {
        or {
          and {
            isRoot()
            notTrashed()
          }
          isStarred()
        }
      }.getQuery() shouldBe "'root' in parents and trashed = false or starred = true"

      GoogleDriveQueryBuilder().apply {
        and {
          or {
            isRoot()
            notTrashed()
            isStarred()
          }
          or {
            isRoot()
            notTrashed()
            isStarred()
          }
          grouped {
            and {
              target("hello") contains value("world")
              target("something") has escapedValue("some'hing")
            }
          }
        }
      }.getQuery() shouldBe "" +
        "'root' in parents or trashed = false or starred = true" +
        " and " +
        "'root' in parents or trashed = false or starred = true" +
        " and " +
        "(hello contains 'world' and 'some'hing' in something)"


      GoogleDriveQueryBuilder().apply {
        grouped {
          grouped {
            grouped {
              isStarred()
            }
          }
        }
      }.getQuery() shouldBe "(((starred = true)))"
    }
  }

  describe("Live service tests.") {
    it("Works on live Drive requests.") {
      val drive = GoogleDriveService.create(ServiceInitializer.createDefault())
      val found = drive.service.files().list().buildQuery {
        and {
          isRoot()
          notTrashed()
        }
      }.execute().files

      found.shouldNotBeEmpty()
    }
  }
})
