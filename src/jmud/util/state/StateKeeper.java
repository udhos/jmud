/*-GNU-GPL-BEGIN-*
  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*-GNU-GPL-END-*/

// $Id: StateKeeper.java,v 1.1.1.1 2001/11/14 09:01:53 fmatheus Exp $

package jmud.util.state;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;

import jmud.util.Separators;
import jgp.container.Vector;
import jmud.util.file.InvalidFileFormatException;
import jmud.util.file.LineReader;
import jmud.util.pair.StrPair;

/*
  [
  #user1
  key1
  info1
  ...
  keyN
  infoN
  $
  ...
  #userN
  ...
  $
  %
  ]
 */

public class StateKeeper {

  private String fileName = null;
  private Vector users    = new Vector();

  public StateKeeper(String fname)
    throws IOException, InvalidFileFormatException {
      fileName = fname;
      File f = new File(fileName);
      if (f.canRead()) {
	LineReader reader = new LineReader(new FileReader(f));
	readNodes(reader);
	reader.close();
      }
  }

  private void readNodes(LineReader reader)
    throws IOException, InvalidFileFormatException {
    for (; ; ) {
      if (reader.eos())
	throw new InvalidFileFormatException();
      String id = reader.readLine();
      if (id.startsWith(Separators.EOF))
	break;
      if (!id.startsWith(Separators.BOR))
	throw new InvalidFileFormatException();

      users.insert(new UserNode(id.substring(1), reader));
    }
  }

  private void writeNodes(BufferedWriter writer) throws IOException {
    for (Enumeration nodes = users.elements(); nodes.hasMoreElements(); ) {
      UserNode curr = (UserNode) nodes.nextElement();
      curr.save(writer);
    }
    writer.write(Separators.EOF); writer.newLine();
  }

  private UserNode findNodeById(String id) throws NoSuchElementException {
    for (Enumeration nodes = users.elements(); nodes.hasMoreElements(); ) {
      UserNode curr = (UserNode) nodes.nextElement();
      if (id.equals(curr.getUserId()))
	return curr;
    }
    throw new NoSuchElementException();
  }

    public Enumeration findPairsById(String id) {
	try {
	    return findNodeById(id).getPairs();
	}
	catch(NoSuchElementException e) {
	}
	return null;
    }

  // registra usuário
  public void register(StateUser user) {
    UserNode node = null;
    try {
      node = findNodeById(user.getStateUserId());
      /* node.getUser() != null => registro duplicado (idempotente) */
      node.setUser(user);
    }
    catch(NoSuchElementException e) {
      users.insert(new UserNode(user));
    }
  }

  // remove usuário
  public void unregister(StateUser user) {
    UserNode node = null;
    try {
      node = findNodeById(user.getStateUserId());
      users.remove(node);
    }
    catch(NoSuchElementException e) {
      /* ignora usuário não registrado (idempotente) */
    }
  }

  public void updateInfo(StateUser user, String key, String info) {
    UserNode node = null;
    try {
      node = findNodeById(user.getStateUserId());
      StateUser u = node.getUser();
      if (u == null)
	register(user); // registra usuário se ele ainda não foi re-registrado
      else if (u != user)
	return;         // ignora se id do usuário não confere
      node.updateInfo(key, info);
    }
    catch(NoSuchElementException e) {
      register(user); // o usuário não foi registrado
    }
  }

  public void removeInfo(StateUser user, String key) {
    UserNode node = null;
    try {
      node = findNodeById(user.getStateUserId());
      StateUser u = node.getUser();
      if (u != user) /* u == null || u != user */
	return;
      node.removeInfo(key);
    }
    catch(NoSuchElementException e) {
    }
  }

  public String lookupInfo(StateUser user, String key) throws NoSuchElementException {
    UserNode node = null;
    try {
      node = findNodeById(user.getStateUserId());
      StateUser u = node.getUser();
      if (u == null)
	register(user);
      else if (u != user)
	return null;
      NodePair pair = node.findPairByKey(key);
      if (pair == null)
	throw new NoSuchElementException();
      return pair.getInfo();
    }
    catch(NoSuchElementException e) {
      return null; // ignora usuário não registrado
    }
  }

  public void requestUpdate() {
    for (Enumeration nodes = users.elements(); nodes.hasMoreElements(); ) {
      UserNode curr = (UserNode) nodes.nextElement();
      StateUser user = curr.getUser();
      if (user != null)
	user.updateStateKeeper(this);
    }
  }

  public void save() throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    writeNodes(writer);
    writer.close();
  }

  public void shutdown() throws IOException {
    requestUpdate();
    save();
  }

  protected void finalize() throws Throwable {
    shutdown();
  }
}

class UserNode {
  private StateUser theUser  = null;
  private String    theId    = null;
  private Vector    thePairs = new Vector();

  UserNode(StateUser user) {
    theUser = user;
    theId   = theUser.getStateUserId();
  }

  UserNode(String id, LineReader reader)
    throws IOException, InvalidFileFormatException {
      theUser = null;
      theId   = id;
      for (; ; ) {
	if (reader.eos())
	  throw new InvalidFileFormatException();
	String key = reader.readLine();
	if (key.startsWith(Separators.EOR))
	  break;
	String info = reader.readLine();
	thePairs.insert(new NodePair(key, info));
      }
  }

  void setUser(StateUser user) {
    theUser = user;
  }

  void save(BufferedWriter writer) throws IOException {
    writer.write(Separators.BOR); writer.write(theId); writer.newLine();
    for (Enumeration pairs = thePairs.elements(); pairs.hasMoreElements(); ) {
      NodePair pair = (NodePair) pairs.nextElement();
      pair.save(writer);
    }
    writer.write(Separators.EOR); writer.newLine();
  }

  StateUser getUser() {
    return theUser;
  }

  String getUserId() {
    return theId;
  }

  NodePair findPairByKey(String key) {
    for (Enumeration pairs = thePairs.elements(); pairs.hasMoreElements(); ) {
      NodePair pair = (NodePair) pairs.nextElement();
      if (key.equals(pair.getKey()))
	return pair;
    }
    return null;
  }

  void updateInfo(String key, String info) {
    NodePair pair = findPairByKey(key);
    if (pair == null)
      thePairs.insert(new NodePair(key, info));
    else
      pair.updateInfo(info);
  }

  void removeInfo(String key) {
    NodePair pair = findPairByKey(key);
    if (pair != null)
      thePairs.remove(pair);
  }

    Enumeration getPairs() {
	return thePairs.elements();
    }
}

class NodePair extends StrPair {

  NodePair(String key, String info) {
    setStr1(key);
    setStr2(info);
  }

  void save(BufferedWriter writer) throws IOException {
    writer.write(getKey());  writer.newLine();
    writer.write(getInfo()); writer.newLine();
  }

  String getKey() {
    return getStr1();
  }

  String getInfo() {
    return getStr2();
  }

  void updateInfo(String info) {
    setStr2(info);
  }
}
