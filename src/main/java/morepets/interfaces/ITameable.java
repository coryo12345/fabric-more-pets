package morepets.interfaces;

import java.util.UUID;

public interface ITameable {
    public void setOwner(UUID owner);

    public UUID getOwner();
}
