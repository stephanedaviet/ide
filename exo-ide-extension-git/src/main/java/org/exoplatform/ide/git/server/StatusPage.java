/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.git.server;

import org.exoplatform.ide.git.shared.GitFile;
import org.exoplatform.ide.git.shared.Status;
import org.exoplatform.ide.git.shared.StatusRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: StatusPage.java 79938 2012-02-28 09:34:46Z andrew00x $
 */
public class StatusPage extends Status implements InfoPage
{
   private final StatusRequest request;

   public StatusPage(String branchName, List<GitFile> changedNotUpdated, List<GitFile> changedNotCommited,
                     List<GitFile> untracked, StatusRequest request)
   {
      super(branchName, changedNotUpdated, changedNotCommited, untracked);
      this.request = request;
   }

   /** @see org.exoplatform.ide.git.server.InfoPage#writeTo(java.io.OutputStream) */
   public void writeTo(OutputStream out) throws IOException
   {
      if (request.isShortFormat())
      {
         writeShortStatus(out);
      }
      else
      {
         writeStatus(out);
      }
   }

   private void writeShortStatus(OutputStream out)
   {
      if ((changedNotUpdated == null || changedNotUpdated.isEmpty()) //
         && (changedNotCommited == null || changedNotCommited.isEmpty()) //
         && (untracked == null || untracked.isEmpty())) //
      {
         return;
      }

      PrintWriter writer = new PrintWriter(out);

      if (changedNotUpdated != null && !changedNotUpdated.isEmpty())
      {
         for (GitFile f : changedNotUpdated)
         {
            writer.format(" %1$s %2$s\n", f.getStatus().getShortStatus(), f.getPath());
         }
      }

      if (changedNotCommited != null && !changedNotCommited.isEmpty())
      {
         for (GitFile f : changedNotCommited)
         {
            writer.format("%1$s  %2$s\n", f.getStatus().getShortStatus(), f.getPath());
         }
      }

      if (untracked != null && !untracked.isEmpty())
      {
         for (GitFile f : untracked)
         {
            writer.format("%1$s %2$s\n", f.getStatus().getShortStatus(), f.getPath());
         }
      }

      writer.flush();
   }

   private void writeStatus(OutputStream out) throws IOException
   {
      PrintWriter writer = new PrintWriter(out);

      writer.format("# On branch %s\n", branchName);
      if (changedNotCommited != null && !changedNotCommited.isEmpty())
      {
         writer.println("# Changes to be committed:");
         writer.println('#');
         for (GitFile f : changedNotCommited)
         {
            writer.format("#       %1$s:    %2$s\n", f.getStatus().getLongStatus(), f.getPath());
         }
      }

      if (changedNotUpdated != null && !changedNotUpdated.isEmpty())
      {
         writer.println('#');
         writer.println("# Changes not staged for commit:");
         writer.println('#');
         for (GitFile f : changedNotUpdated)
         {
            writer.format("#       %1$s:    %2$s\n", f.getStatus().getLongStatus(), f.getPath());
         }
      }

      if (untracked != null && !untracked.isEmpty())
      {
         writer.println('#');
         writer.println("# Untracked files:");
         writer.println('#');
         for (GitFile f : untracked)
         {
            writer.format("#       %s\n", f.getPath());
         }
      }

      if (changedNotCommited == null || changedNotCommited.isEmpty())
      {
         if ((untracked != null && !untracked.isEmpty()) || (changedNotUpdated != null && !changedNotUpdated.isEmpty()))
         {
            // If has some changes but they are not in index.
            writer.println("no changes added to commit");
         }
         else
         {
            // If there is no changes at all.
            writer.println("nothing to commit (working directory clean)");
         }
      }

      writer.flush();
   }
}
