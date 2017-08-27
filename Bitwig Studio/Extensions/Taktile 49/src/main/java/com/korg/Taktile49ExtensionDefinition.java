package com.korg;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class Taktile49ExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("83b874d3-568b-42c4-82c1-38ec1e6709fe");
   
   public Taktile49ExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "Taktile 49";
   }
   
   @Override
   public String getAuthor()
   {
      return "Jeremy Hinds";
   }

   @Override
   public String getVersion()
   {
      return "0.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "Korg";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "Taktile 49";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 3;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 3;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 3;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
      list.add(new String[]{"taktile-49 MIDI 1", "taktile-49 MIDI 2", "taktile-49 MIDI 3"},
               new String[]{"taktile-49 MIDI 1", "taktile-49 MIDI 2", "taktile-49 MIDI 3"});
      /*
      if (platformType == PlatformType.WINDOWS)
      {
         // TODO: Set the correct names of the ports for auto detection on Windows platform here
         // and uncomment this when port names are correct.
         // list.add(new String[]{"Input Port 0", "Input Port 1", "Input Port 2"}, new String[]{"Output Port 0", "Output Port 1", "Output Port 2"});
      }
      else if (platformType == PlatformType.MAC)
      {
         // TODO: Set the correct names of the ports for auto detection on Windows platform here
         // and uncomment this when port names are correct.
         // list.add(new String[]{"Input Port 0", "Input Port 1", "Input Port 2"}, new String[]{"Output Port 0", "Output Port 1", "Output Port 2"});
      }
      else if (platformType == PlatformType.LINUX)
      {
         // TODO: Set the correct names of the ports for auto detection on Windows platform here
         // and uncomment this when port names are correct.
         // list.add(new String[]{"Input Port 0", "Input Port 1", "Input Port 2"}, new String[]{"Output Port 0", "Output Port 1", "Output Port 2"});
      }
      */
   }

   @Override
   public Taktile49Extension createInstance(final ControllerHost host)
   {
      return new Taktile49Extension(this, host);
   }
}
