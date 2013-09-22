package li.cil.oc.client.computer

import li.cil.oc.common.computer.IComputer
import net.minecraft.nbt.NBTTagCompound

/**
 * This is a dummy class for the client side. It does nothing, really, just
 * saves us a couple of side checks.
 */
class Computer(val owner: AnyRef) extends IComputer {
  override def start() = false

  override def stop() = false

  override var isRunning = false

  override def update() {}

  override def signal(name: String, args: Any*) = throw new NotImplementedError

  override def readFromNBT(nbt: NBTTagCompound) {}

  override def writeToNBT(nbt: NBTTagCompound) {}
}