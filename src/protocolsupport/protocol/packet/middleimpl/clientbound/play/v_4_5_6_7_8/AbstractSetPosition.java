package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_4_5_6_7_8;

import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.packet.middle.clientbound.play.MiddleSetPosition;
import protocolsupport.protocol.storage.netcache.MovementCache;

public abstract class AbstractSetPosition extends MiddleSetPosition {

	protected final MovementCache movementCache = cache.getMovementCache();

	public AbstractSetPosition(ConnectionImpl connection) {
		super(connection);
	}

	@Override
	public boolean postFromServerRead() {
		if (teleportConfirmId != 0) {
			cache.getMovementCache().setTeleportLocation(x, y, z, teleportConfirmId);
		}
		return true;
	}

}
