package io.imtony.vdrive.fxterm

import javafx.scene.text.Font
import mu.KLogging

object FontLocker {
  private data class Key(val font: AppFonts, val size: Int)

  private val log: KLogging = object : KLogging() {}

  private val loadedSizes: MutableMap<AppFonts, Set<Int>> = mutableMapOf()
  private val loadedFonts: MutableMap<Key, Font> = mutableMapOf()

  fun getFont(fontType: AppFonts, size: Int): Font {
    log.logger.info { "Searching for fontType '$fontType' with size '$size'" }
    if (loadedSizes[fontType]?.contains(size) == true) {
      val font = loadedFonts[Key(fontType, size)]

      if (font != null) {
        log.logger.info { "Found font $font" }
        return font
      } else {
        log.logger.info { "LoadedSizes contains size entry but not found from dictionary." }
        loadedSizes[fontType]?.minus(size)
      }
    }

    val font: Font = Font.loadFont(fontType.getAppResource().urlString, size.toDouble())
    loadedFonts[Key(fontType, size)] = font
    return font
  }
}

enum class AppFonts {
  ActorRegular,
  Consola,
  GadugiBold,
  LatoRegular,
  Overpass,
  UbuntuBook;

  fun getAppResource(): AppResource = when (this) {
    ActorRegular -> Resources.Fonts.ActorRegular.Instance
    Consola -> Resources.Fonts.Consola.Instance
    GadugiBold -> Resources.Fonts.GadugiBold.Instance
    LatoRegular -> Resources.Fonts.LatoRegular.Instance
    Overpass -> Resources.Fonts.Overpass.Instance
    UbuntuBook -> Resources.Fonts.UbuntuBook.Instance
  }
}
