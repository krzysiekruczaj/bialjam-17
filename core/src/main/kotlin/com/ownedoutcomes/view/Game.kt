package com.ownedoutcomes.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.ownedoutcomes.fieldHeight
import com.ownedoutcomes.fieldWidth
import com.ownedoutcomes.stageHeight
import com.ownedoutcomes.stageWidth
import com.ownedoutcomes.view.actor.group
import ktx.actors.centerPosition
import ktx.app.KtxScreen
import ktx.scene2d.buttonGroup
import ktx.scene2d.image
import ktx.scene2d.imageButton
import ktx.scene2d.table

class Game(
  val stage: Stage
) : KtxScreen {
  val spritesView = group {
    image("chicken2_v1") {
      height = 75f
      width = 75f
      rotation = -30f
      centerPosition(stageWidth, stageHeight, true)
    }

    image("chicken4_v1") {
      height = 75f
      width = 75f
      rotation = 30f
      x = stageWidth / 2 - 100f
      y = stageHeight / 2 - 100f
    }
  }
  val view = table {
    setFillParent(true)
    align(Align.bottomLeft)
    // Game:
    val bottomPadding = -110f
    table {
      val tilesY = (stageHeight.toInt() / fieldHeight)
      val tilesX = (stageWidth.toInt() / fieldWidth)

      val centerX = tilesX / 2

      val fieldRadius = 3
      val dirtStart = centerX - fieldRadius
      val dirtEnd = centerX + fieldRadius
      for (y in 0..tilesY - 1) {
        for (x in 0..tilesX - 1) {
          imageButton(style = createStyle(x, y, dirtStart, dirtEnd)) {

          }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
        }
        row()
      }
    }.cell(width = stageWidth, height = stageHeight, padBottom = bottomPadding, row = true)

    // GUI:
    buttonGroup(minCheckedCount = 0, maxCheckedCount = 1) {
      background("brown")
      repeat(5) { index ->
        imageButton(style = "tower$index") {
          it.height(60f).width(70f)
            .padBottom(5f).padTop(5f).padLeft(10f).padRight(10f)
        }
      }
    }.cell(growX = false, height = 90f, pad = 10f)
    pack()
  }

  private fun createStyle(x: Int, y: Int, dirtStart: Int, dirtEnd: Int): String {
    when {
      x == dirtStart && y == dirtStart -> return "grass_nw"
      x == dirtEnd && y == dirtStart -> return "grass_ne"
      x in dirtStart..dirtEnd && y == dirtStart -> return "grass_n"
      x == dirtStart && y == dirtEnd -> return "grass_sw"
      x == dirtEnd && y == dirtEnd -> return "grass_se"
      x in dirtStart..dirtEnd && y == dirtEnd -> return "grass_s"
      x == dirtStart && y in dirtStart..dirtEnd -> return "grass_w"
      x == dirtEnd && y in dirtStart..dirtEnd -> return "grass_e"
      x in dirtStart..dirtEnd && y in dirtStart..dirtEnd -> return "dirt"
      else -> return "grass"
    }
  }

  override fun show() {
    reset()
    stage.addActor(view)
    stage.addActor(spritesView)
    Gdx.input.inputProcessor = stage
  }

  override fun render(delta: Float) {
    stage.act(delta)
    stage.draw()
  }

  fun reset() {}
}
