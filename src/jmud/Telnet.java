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

package jmud;


class Telnet {

    static final byte IAC     = (byte) 255; // interpret as command:
    static final byte DONT    = (byte) 254; // you are not to use option
    static final byte DO      = (byte) 253; // please, you use option
    static final byte WONT    = (byte) 252; // I won't use option
    static final byte WILL    = (byte) 251; // I will use option
    static final byte SB      = (byte) 250; // interpret as subnegotiation
    static final byte GA      = (byte) 249; // you may reverse the line
    static final byte EL      = (byte) 248; // erase the current line
    static final byte EC      = (byte) 247; // erase the current character
    static final byte AYT     = (byte) 246; // are you there
    static final byte AO      = (byte) 245; // abort output--but let prog finish
    static final byte IP      = (byte) 244; // interrupt process--permanently
    static final byte BREAK   = (byte) 243; // break
    static final byte DM      = (byte) 242; // data mark--for connect. cleaning
    static final byte NOP     = (byte) 241; // nop
    static final byte SE      = (byte) 240; // end sub negotiation
    static final byte EOR     = (byte) 239; // end of record (transparent mode)
    static final byte ABORT   = (byte) 238; // Abort process
    static final byte SUSP    = (byte) 237; // Suspend process
    static final byte EOF     = (byte) 236; // End of file

    static final byte TELOPT_BINARY         = (byte) 0;  // 8-bit data path
    static final byte TELOPT_ECHO           = (byte) 1;  // echo
    static final byte TELOPT_RCP            = (byte) 2;  // prepare to reconnect
    static final byte TELOPT_SGA            = (byte) 3;  // suppress go ahead
    static final byte TELOPT_NAMS           = (byte) 4;  // approximate message size
    static final byte TELOPT_STATUS         = (byte) 5;  // give status
    static final byte TELOPT_TM             = (byte) 6;  // timing mark
    static final byte TELOPT_RCTE           = (byte) 7;  // remote controlled transmission and echo

    // negotiate about:
    static final byte TELOPT_NAOL           = (byte) 8;  // output line width
    static final byte TELOPT_NAOP           = (byte) 9;  // output page size
    static final byte TELOPT_NAOCRD         = (byte) 10; // CR disposition
    static final byte TELOPT_NAOHTS         = (byte) 11; // horizontal tabstops
    static final byte TELOPT_NAOHTD         = (byte) 12; // horizontal tab disposition
    static final byte TELOPT_NAOFFD         = (byte) 13; // formfeed disposition
    static final byte TELOPT_NAOVTS         = (byte) 14; // vertical tab stops
    static final byte TELOPT_NAOVTD         = (byte) 15; // vertical tab disposition
    static final byte TELOPT_NAOLFD         = (byte) 16; // output LF disposition

    static final byte TELOPT_XASCII         = (byte) 17; // extended asc character set
    static final byte TELOPT_LOGOUT         = (byte) 18; // force logout
    static final byte TELOPT_BM             = (byte) 19; // byte macro
    static final byte TELOPT_DET            = (byte) 20; // data entry terminal
    static final byte TELOPT_SUPDUP         = (byte) 21; // supdup protocol
    static final byte TELOPT_SUPDUPOUTPUT   = (byte) 22; // supdup output
    static final byte TELOPT_SNDLOC         = (byte) 23; // send location
    static final byte TELOPT_TTYPE          = (byte) 24; // terminal type
    static final byte TELOPT_EOR            = (byte) 25; // end or record
    static final byte TELOPT_TUID           = (byte) 26; // TACACS user identification
    static final byte TELOPT_OUTMRK         = (byte) 27; // output marking
    static final byte TELOPT_TTYLOC         = (byte) 28; // terminal location number
    static final byte TELOPT_3270REGIME     = (byte) 29; // 3270 regime
    static final byte TELOPT_X3PAD          = (byte) 30; // X.3 PAD
    static final byte TELOPT_NAWS           = (byte) 31; // window size
    static final byte TELOPT_TSPEED         = (byte) 32; // terminal speed
    static final byte TELOPT_LFLOW          = (byte) 33; // remote flow control
    static final byte TELOPT_LINEMODE       = (byte) 34; // Linemode option
    static final byte TELOPT_XDISPLOC       = (byte) 35; // X Display Location
    static final byte TELOPT_OLD_ENVIRON    = (byte) 36; // Old: Environment variables
    static final byte TELOPT_AUTHENTICATION = (byte) 37; // Authenticate
    static final byte TELOPT_ENCRYPT        = (byte) 38; // Encryption option
    static final byte TELOPT_NEW_ENVIRON    = (byte) 39; // New: Environment variables
    static final byte TELOPT_EXOPL          = (byte) 255;// extended-options-list
}

