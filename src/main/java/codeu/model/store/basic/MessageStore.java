// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.model.store.basic;

import codeu.model.data.Message;
import codeu.model.store.persistence.PersistentDataStoreException;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class MessageStore {

  /** Singleton instance of MessageStore. */
  private static MessageStore instance;

  /**
   * Returns the singleton instance of MessageStore that should be shared between all servlet
   * classes. Do not call this function from a test; use getTestInstance() instead.
   */
  public static MessageStore getInstance() {
    if (instance == null) {
      instance = new MessageStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static MessageStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new MessageStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Messages from and saving Messages to
   * Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Messages. */
  private List<Message> messages;

  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private MessageStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    messages = new ArrayList<>();
  }

  /**
   * Load Default Message objects.
   *
   * @return false if an error occurs.
   */
  public boolean loadTestData() {
    messages = new ArrayList<>();
    boolean loaded = false;
    try {
      messages.addAll(DefaultDataStore.getInstance().getAllMessages());
      loaded = true;
    } catch (Exception e) {
      loaded = false;
      System.out.println("ERROR: Unable to establish initial store (messages).");
    }
    return loaded;
  }


  /**
   * Load Romeo and Juliet Message objects.
   *
   * @return false if an error occurs.
   */
  public boolean loadRomeoData() {
    messages = new ArrayList<>();
    boolean loaded = false;
    try {
      messages.addAll(RomeoDataStore.getInstance().getAllMessages());
      loaded = true;
    } catch (Exception e) {
      loaded = false;
      System.out.println("ERROR: Unable to establish initial store (messages).");
    }
    return loaded;
  }

  /**
   * Load Great Gatsby Message objects.
   *
   * @return false if an error occurs.
   */
  public boolean loadGatsbyData() {
    messages = new ArrayList<>();
    boolean loaded = false;
    try {
      messages.addAll(GatsbyDataStore.getInstance().getAllMessages());
      loaded = true;
    } catch (Exception e) {
      loaded = false;
      System.out.println("ERROR: Unable to establish initial store (messages).");
    }
    return loaded;
  }

  /**
   * Load Of Mice and Men Message objects.
   *
   * @return false if an error occurs.
   */
  public boolean loadMiceData() {
    messages = new ArrayList<>();
    boolean loaded = false;
    try {
      messages.addAll(MiceDataStore.getInstance().getAllMessages());
      loaded = true;
    } catch (Exception e) {
      loaded = false;
      System.out.println("ERROR: Unable to establish initial store (messages).");
    }
    return loaded;
  }

  /** Add a new message to the current set of messages known to the application. */
  public void addMessage(Message message) {
    messages.add(message);
    persistentStorageAgent.writeThrough(message);
  }

  /** Access the current set of Messages within the given Conversation. */
  public List<Message> getMessagesInConversation(UUID conversationId) {

    List<Message> messagesInConversation = new ArrayList<>();

    for (Message message : messages) {
      if (message.getConversationId().equals(conversationId)) {
        messagesInConversation.add(message);
      }
    }

    return messagesInConversation;
  }

  /** Returns number of messages in database */
  public int getNumMessages() {
    return this.messages.size();
  }

  /** Sets the List of Messages stored by this MessageStore. */
  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  /**
   * Returns a copy of the list of all messages.
   */
  public List<Message> getMessages() {
    return this.messages;
  }

  /**
   * Returns a copy of the list of messages written by a specific user
   */
  public List<Message> getMessagesByUser(UUID userId) {

    try {
      return PersistentStorageAgent.getInstance().loadMessagesByUser(userId);
    } catch (PersistentDataStoreException e) {
      System.err.println("Server didn't start correctly. An error occurred during Datastore load!");
      System.err.println("This is usually caused by loading data that's in an invalid format.");
      System.err.println("Check the stack trace to see exactly what went wrong.");
      throw new RuntimeException(e);
    }
  }
}
