package li.cil.oc.server

import cpw.mods.fml.common.network.Player
import li.cil.oc.common.PacketBuilder
import li.cil.oc.common.PacketType
import li.cil.oc.common.tileentity._
import li.cil.oc.util.PackedColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.ForgeDirection
import scala.Some

/** Centralized packet dispatcher for sending updates to the client. */
object PacketSender {
  def sendAnalyze(stats: NBTTagCompound, address: String, player: Player) {
    val pb = new PacketBuilder(PacketType.Analyze)

    pb.writeNBT(stats)
    pb.writeUTF(address)

    pb.sendToPlayer(player)
  }

  def sendComputerState(t: TileEntity, value: Boolean, player: Option[Player] = None) {
    val pb = new PacketBuilder(PacketType.ComputerStateResponse)

    pb.writeTileEntity(t)
    pb.writeBoolean(value)

    player match {
      case Some(p) => pb.sendToPlayer(p)
      case _ => pb.sendToAllPlayers()
    }
  }

  def sendPowerState(t: PowerInformation, player: Option[Player] = None) {
    val pb = new PacketBuilder(PacketType.PowerStateResponse)

    pb.writeTileEntity(t)
    pb.writeDouble(t.globalBuffer)
    pb.writeDouble(t.globalBufferSize)

    player match {
      case Some(p) => pb.sendToPlayer(p)
      case _ => pb.sendToAllPlayers()
    }
  }

  def sendRedstoneState(t: Redstone, player: Option[Player] = None) {
    val pb = new PacketBuilder(PacketType.RedstoneStateResponse)

    pb.writeTileEntity(t)
    pb.writeBoolean(t.isOutputEnabled)
    for (d <- ForgeDirection.VALID_DIRECTIONS) {
      pb.writeByte(t.output(d))
    }

    player match {
      case Some(p) => pb.sendToPlayer(p)
      case _ => pb.sendToAllPlayers()
    }
  }

  def sendRobotMove(t: Robot, ox: Int, oy: Int, oz: Int, direction: ForgeDirection) {
    val pb = new PacketBuilder(PacketType.RobotMove)

    // Custom pb.writeTileEntity() with fake coordinates (valid for the client).
    pb.writeInt(t.proxy.world.provider.dimensionId)
    pb.writeInt(ox)
    pb.writeInt(oy)
    pb.writeInt(oz)
    pb.writeDirection(direction)

    pb.sendToAllPlayers()
  }

  def sendRobotAnimateSwing(t: Robot) {
    val pb = new PacketBuilder(PacketType.RobotAnimateSwing)

    pb.writeTileEntity(t.proxy)
    pb.writeInt(t.animationTicksTotal)

    pb.sendToNearbyPlayers(t.proxy)
  }

  def sendRobotAnimateTurn(t: Robot) {
    val pb = new PacketBuilder(PacketType.RobotAnimateTurn)

    pb.writeTileEntity(t.proxy)
    pb.writeByte(t.turnAxis)
    pb.writeInt(t.animationTicksTotal)

    pb.sendToNearbyPlayers(t.proxy)
  }

  def sendRobotEquippedItemChange(t: Robot, stack: ItemStack) {
    val pb = new PacketBuilder(PacketType.RobotEquippedItemChange)

    pb.writeTileEntity(t.proxy)
    pb.writeItemStack(stack)

    pb.sendToAllPlayers()
  }

  def sendRobotSelectedSlotChange(t: Robot) {
    val pb = new PacketBuilder(PacketType.RobotSelectedSlotChange)

    pb.writeTileEntity(t.proxy)
    pb.writeInt(t.selectedSlot)

    pb.sendToAllPlayers()
  }

  def sendRobotState(t: Robot, player: Option[Player] = None) {
    val pb = new PacketBuilder(PacketType.RobotStateResponse)

    pb.writeTileEntity(t.proxy)
    pb.writeInt(t.selectedSlot)
    pb.writeItemStack(t.getStackInSlot(0))
    pb.writeInt(t.animationTicksTotal)
    pb.writeInt(t.animationTicksLeft)
    pb.writeDirection(t.moveDirection)
    pb.writeBoolean(t.swingingTool)
    pb.writeByte(t.turnAxis)

    player match {
      case Some(p) => pb.sendToPlayer(p)
      case _ => pb.sendToAllPlayers()
    }
  }

  def sendRotatableState(t: Rotatable, player: Option[Player] = None) {
    val pb = new PacketBuilder(PacketType.RotatableStateResponse)

    pb.writeTileEntity(t)
    pb.writeDirection(t.pitch)
    pb.writeDirection(t.yaw)

    player match {
      case Some(p) => pb.sendToPlayer(p)
      case _ => pb.sendToAllPlayers()
    }
  }

  def sendScreenBufferState(t: Buffer, player: Option[Player] = None) {
    val pb = new PacketBuilder(PacketType.ScreenBufferResponse)

    pb.writeTileEntity(t)

    val screen = t.buffer
    val (w, h) = screen.resolution
    pb.writeInt(w)
    pb.writeInt(h)
    pb.writeUTF(screen.text)
    pb.writeInt(screen.depth.id)
    pb.writeInt(screen.foreground)
    pb.writeInt(screen.background)
    for (cs <- screen.color) for (c <- cs) pb.writeShort(c)

    player match {
      case Some(p) => pb.sendToPlayer(p)
      case _ => pb.sendToAllPlayers()
    }
  }

  def sendScreenColorChange(t: Buffer, foreground: Int, background: Int) {
    val pb = new PacketBuilder(PacketType.ScreenColorChange)

    pb.writeTileEntity(t)
    pb.writeInt(foreground)
    pb.writeInt(background)

    pb.sendToAllPlayers()
  }

  def sendScreenCopy(t: Buffer, col: Int, row: Int, w: Int, h: Int, tx: Int, ty: Int) {
    val pb = new PacketBuilder(PacketType.ScreenCopy)

    pb.writeTileEntity(t)
    pb.writeInt(col)
    pb.writeInt(row)
    pb.writeInt(w)
    pb.writeInt(h)
    pb.writeInt(tx)
    pb.writeInt(ty)

    pb.sendToAllPlayers()
  }

  def sendScreenDepthChange(t: Buffer, value: PackedColor.Depth.Value) {
    val pb = new PacketBuilder(PacketType.ScreenDepthChange)

    pb.writeTileEntity(t)
    pb.writeInt(value.id)

    pb.sendToAllPlayers()
  }

  def sendScreenFill(t: Buffer, col: Int, row: Int, w: Int, h: Int, c: Char) {
    val pb = new PacketBuilder(PacketType.ScreenFill)

    pb.writeTileEntity(t)
    pb.writeInt(col)
    pb.writeInt(row)
    pb.writeInt(w)
    pb.writeInt(h)
    pb.writeChar(c)

    pb.sendToAllPlayers()
  }

  def sendScreenPowerChange(t: Buffer, hasPower: Boolean) {
    val pb = new PacketBuilder(PacketType.ScreenPowerChange)

    pb.writeTileEntity(t)
    pb.writeBoolean(hasPower)

    pb.sendToAllPlayers()
  }

  def sendScreenResolutionChange(t: Buffer, w: Int, h: Int) {
    val pb = new PacketBuilder(PacketType.ScreenResolutionChange)

    pb.writeTileEntity(t)
    pb.writeInt(w)
    pb.writeInt(h)

    pb.sendToAllPlayers()
  }

  def sendScreenSet(t: Buffer, col: Int, row: Int, s: String) {
    val pb = new PacketBuilder(PacketType.ScreenSet)

    pb.writeTileEntity(t)
    pb.writeInt(col)
    pb.writeInt(row)
    pb.writeUTF(s)

    pb.sendToAllPlayers()
  }
}