package ru.mipt1c.homework.task1;

import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import java.io.*;

public class KeyValueStorageImpl<K, V> implements KeyValueStorage<K, V> {
    private HashMap<K, V> cache = new HashMap<>();
    private File storage;
    private File bytes;
    private final String path;
    private boolean isClosed = false;
    public static final String FILE = "storage.dat";
    public static final String BYTES = "bytes.dat";

    @SuppressWarnings("unchecked")
    public KeyValueStorageImpl(String path) throws MalformedDataException {
        this.path = path;
        this.storage = new File(path + File.separator + FILE);
        this.bytes = new File(path + File.separator + BYTES);
        try (ObjectInputStream objectInPutStream1 = new ObjectInputStream(Files.newInputStream(storage.toPath()));) {

            if (this.storage.createNewFile() && this.bytes.createNewFile()) {
                System.out.println("Init: File created successfully");
            } else {
                System.out.println("Init: File exists");
                byte[] bytesStorage = FileUtils.readFileToByteArray(this.storage);
                byte[] byteArr = FileUtils.readFileToByteArray(this.bytes);

                if (!Arrays.equals(bytesStorage, byteArr)) {
                    throw new MalformedDataException("Init: The storage was changed");
                }
            }
            this.cache = (HashMap<K, V>) objectInPutStream1.readObject();
        } catch (IOException | ClassNotFoundException | MalformedDataException exception) {
            System.out.println("Init: Cached successful");
            if (exception instanceof ClassNotFoundException) {
                exception.printStackTrace();
            }

            if (exception instanceof MalformedDataException) {
                throw new MalformedDataException("Init: The storage was changed");
            }
        }
    }

    @Override
    public V read(K key) {
        if (!isClosed) {
            return this.cache.get(key);
        } else {
            throw new ClosedStorageException("Storage is already closed");
        }
    }

    @Override
    public boolean exists(K key) {
        if (!isClosed) {
            return this.cache.containsKey(key);
        } else {
            throw new ClosedStorageException("Storage is already closed");
        }
    }

    @Override
    public void write(K key, V value) {
        if (!isClosed) {
            this.cache.put(key, value);
        } else {
            throw new ClosedStorageException("Storage is already closed");
        }
    }

    @Override
    public void delete(K key) {
        if (!isClosed) {
            this.cache.remove(key);
        } else {
            throw new ClosedStorageException("Storage is already closed");
        }
    }

    @Override
    public Iterator<K> readKeys() {
        if (!isClosed) {
            return this.cache.keySet().iterator();
        } else {
            throw new ClosedStorageException("Storage is already closed");
        }
    }

    @Override
    public int size() {
        if (!isClosed) {
            return this.cache.size();
        } else {
            throw new ClosedStorageException("Storage is already closed");
        }
    }

    @Override
    public void close() {
        if (!isClosed) {
            flush();
            this.isClosed = true;
            System.out.println("Close: successful closed");
        }
    }

    @Override
    public void flush() {
        if (!isClosed) {
            try (ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(
                    Files.newOutputStream(storage.toPath()))) {
                objectOutputStream1.writeObject(this.cache);
                objectOutputStream1.flush();
                System.out.println("Flush: successful flushed");
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            if (this.bytes.delete()) {
                System.out.println("Flush: successfully deleted bytes file");
            }

            try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(storage.toPath()))) {
                File file = new File(this.path + File.separator + BYTES);
                file.createNewFile();
                ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(Files.newOutputStream(file.toPath()));
                objectOutputStream1.writeObject(objectInputStream.readObject());
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }

        }
    }
}