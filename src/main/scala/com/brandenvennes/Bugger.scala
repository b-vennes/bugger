package com.brandenvennes

import com.brandenvennes.generated.{Assets, Config}
import indigo.*
import indigo.scenes.*

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object Bugger extends IndigoGame[BootData, StartUpData, Model, ViewModel]:

  def initialScene(bootData: BootData): Option[SceneName] =
    None

  def scenes(
      bootData: BootData
  ): NonEmptyList[Scene[StartUpData, Model, ViewModel]] =
    NonEmptyList(GameScene)

  val eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(Config.config, BootData())
        .withAssets(Assets.assets.assetSet)
    )

  def initialModel(startupData: StartUpData): Outcome[Model] =
    Outcome(Model.initial(startupData.dice))

  def initialViewModel(
      startupData: StartUpData,
      model: Model
  ): Outcome[ViewModel] =
    Outcome(ViewModel())

  def setup(
      bootData: BootData,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartUpData]] =
    Outcome(Startup.Success(StartUpData(dice)))

  def updateModel(
      context: FrameContext[StartUpData],
      model: Model
  ): GlobalEvent => Outcome[Model] =
    _ => Outcome(model)

  def updateViewModel(
      context: FrameContext[StartUpData],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    _ => Outcome(viewModel)

  def present(
      context: FrameContext[StartUpData],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)

final case class BootData()
final case class StartUpData(dice: Dice)
final case class ViewModel()
