package morepets;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

public class Utils {
    public static UUID uuidFromBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static byte[] uuidArrayToBytes(UUID[] uuids) {
        ByteBuffer bb = ByteBuffer.allocate(16 * uuids.length);
        for (UUID uuid : uuids) {
            byte[] bytes = uuidToBytes(uuid);
            bb.put(bytes);
        }
        return bb.array();
    }

    public static byte[] uuidArrayToBytes(List<UUID> uuids) {
        UUID[] arr = new UUID[uuids.size()];
        return uuidArrayToBytes(uuids.toArray(arr));
    }

    public static UUID[] uuidArrayFromBytes(byte[] bytes) {
        int length = bytes.length / 16;
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        UUID[] uuids = new UUID[bytes.length / 16];
        for (int i = 0; i < length; i++) {
            byte[] b = new byte[16];
            bb.get(b, i * 16, 16);
            uuids[i] = uuidFromBytes(b);
        }
        return uuids;
    }
}
