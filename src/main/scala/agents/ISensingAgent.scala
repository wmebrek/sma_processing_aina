package agents

import jade.core.AID

trait ISensingAgent {
  def addAgentToDest(agent:AID): Unit
}