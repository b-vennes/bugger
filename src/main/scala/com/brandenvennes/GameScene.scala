package com.brandenvennes

import indigo.*
import indigo.scenes.*
import monocle.syntax.all.*

object GameScene extends Scene[StartUpData, Model, ViewModel]:

  type SceneModel     = Model
  type SceneViewModel = ViewModel

  val name: SceneName =
    SceneName("game")

  val modelLens: Lens[Model, Model] =
    Lens.keepLatest

  val viewModelLens: Lens[ViewModel, ViewModel] =
    Lens.keepLatest

  val eventFilters: EventFilters =
    EventFilters.Permissive

  val subSystems: Set[SubSystem[Model]] =
    Set()

  def updateModel(
      context: SceneContext[StartUpData],
      model: SceneModel
  ): GlobalEvent => Outcome[SceneModel] =
    case KeyboardEvent.KeyUp(Key.LEFT_ARROW) => Outcome(model.moveBugslyLeft)
    case KeyboardEvent.KeyUp(Key.RIGHT_ARROW) => Outcome(model.moveBugslyRight)
    case KeyboardEvent.KeyUp(Key.UP_ARROW) => Outcome(model.moveBugslyUp)
    case FrameTick =>
      if model.checkCollision then Outcome(Model.initial(model.dice))
      else Outcome(model.tick(context.delta))
    case _ => Outcome(model)

  def updateViewModel(
      context: SceneContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    _ => Outcome(viewModel)

  val lanes = 10
  val leftBuffer = 50
  val size = 32
  val halfSize = 16

  def present(
      context: SceneContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel
  ): Outcome[SceneUpdateFragment] =
    logger.debugOnce(model.toString())
    Outcome(
      SceneUpdateFragment
        .empty
        .addLayer(
          Shape.Line(Point(leftBuffer - 4, 0), Point(leftBuffer, size * (lanes + 3)), Stroke(2, RGBA.DarkBlue)),
          Shape.Line(Point(leftBuffer + (11 * size) + 4, 0), Point(leftBuffer + (11 * size) + 4, size * (lanes + 3)), Stroke(2, RGBA.DarkBlue)),
        )
        .addLayer(
          Shape.Circle(
            Circle(leftBuffer + (model.bugsly.location.x * size) + halfSize, halfSize + (lanes * size), halfSize),
            Fill.Color(RGBA.Coral)
          )
        )
        .addLayer(
          Batch.fromList(model.cars).map(car =>
            Shape.Box(
              Rectangle(leftBuffer + (car.location.x * size), (lanes - (model.bugsly.location.y - car.location.y)) * size, size, size),
              Fill.Color(RGBA.Magenta)))
        )
        .addLayer(
          TextBox(model.bugsly.location.y.abs.toString())
            .withColor(RGBA.Yellow)
            .scaleBy(Vector2(2, 2))
            .moveTo(Point(10, 10))
        )
    )
