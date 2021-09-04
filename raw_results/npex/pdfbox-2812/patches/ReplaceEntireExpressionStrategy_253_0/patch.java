/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDPatternResources;
import org.apache.pdfbox.pdmodel.common.PDStream;

/**
 * This class represents a color space in a pdf document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.11 $
 */
public final class PDColorSpaceFactory
{
    /**
     * Private constructor for utility classes.
     */
    private PDColorSpaceFactory()
    {
        //utility class should not be implemented
    }

    /**
     * This will create the correct color space given the name.
     *
     * @param colorSpace The color space object.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( COSBase colorSpace ) throws IOException
    {
        return createColorSpace( colorSpace, null );
    }

    /**
     * This will create the correct color space given the name.
     *
     * @param colorSpace The color space object.
     * @param colorSpaces The ColorSpace dictionary from the current resources, if any.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( COSBase colorSpace, Map<String, PDColorSpace> colorSpaces ) 
    throws IOException 
    {
        return createColorSpace( colorSpace, colorSpaces, null );
    }

    /**
     * This will create the correct color space given the name.
     *
     * @param colorSpace The color space object.
     * @param colorSpaces The ColorSpace dictionary from the current resources, if any.
     * @param patterns The patterns dictionary from the current resources, if any
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( COSBase colorSpace, Map<String, PDColorSpace> colorSpaces, 
            Map<String,PDPatternResources> patterns) 
    throws IOException
    {
        PDColorSpace retval = null;
        if (colorSpace instanceof COSObject) 
        {
            retval = createColorSpace( ((COSObject)colorSpace).getObject(), colorSpaces );
        } 
        else if( colorSpace instanceof COSName ) {
            retval = createColorSpace( ((COSName)colorSpace).getName(), colorSpaces );
        }
        else if( colorSpace instanceof COSArray )
        {
            COSArray array = (COSArray)colorSpace;
            String name = ((COSName)array.getObject( 0 )).getName();
            if( name.equals( PDCalGray.NAME ) )
            {
                retval = new PDCalGray( array );
            }
            else if( name.equals( PDDeviceRGB.NAME ) )
            {
                retval = PDDeviceRGB.INSTANCE;
            }
            else if( name.equals( PDDeviceGray.NAME ) )
            {
                retval = new PDDeviceGray();
            }
            else if( name.equals( PDDeviceCMYK.NAME ) )
            {
                retval = PDDeviceCMYK.INSTANCE;
            }
            else if( name.equals( PDCalRGB.NAME ) )
            {
                retval = new PDCalRGB( array );
            }
            else if( name.equals( PDDeviceN.NAME ) )
            {
                retval = new PDDeviceN( array );
            }
            else if( name.equals( PDIndexed.NAME ) )
            {
                retval = new PDIndexed( array );
            }
            else if( name.equals( PDLab.NAME ) )
            {
                retval = new PDLab( array );
            }
            else if( name.equals( PDSeparation.NAME ) )
            {
                retval = new PDSeparation( array );
            }
            else if( name.equals( PDICCBased.NAME ) )
            {
                retval = new PDICCBased( array );
            }
            else if( name.equals( PDPattern.NAME ) )
            {
                retval = new PDPattern( array );
            }
            else
            {
                throw new IOException( "Unknown colorspace array type:" + name );
            }
        }
        else
        {
            throw new IOException( "Unknown colorspace type:" + colorSpace );
        }
        return retval;
    }

    /**
     * This will create the correct color space given the name.
     *
     * @param colorSpaceName The name of the colorspace.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( String colorSpaceName ) throws IOException
    {
        return createColorSpace(colorSpaceName, null);
    }

    /**
     * This will create the correct color space given the name.
     *
     * @param colorSpaceName The name of the colorspace.
     * @param colorSpaces The ColorSpace dictionary from the current resources, if any.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( String colorSpaceName, Map<String, PDColorSpace> colorSpaces ) 
    throws IOException
    {
        PDColorSpace cs = null;
        if( colorSpaceName.equals( PDDeviceCMYK.NAME ) )
        {
            cs = PDDeviceCMYK.INSTANCE;
        }
        else if( colorSpaceName.equals( PDDeviceRGB.NAME ) )
        {
            cs = PDDeviceRGB.INSTANCE;
        }
        else if( colorSpaceName.equals( PDDeviceGray.NAME ) )
        {
            cs = new PDDeviceGray();
        }
        else if( colorSpaces != null && colorSpaces.get( colorSpaceName ) != null )
        {
            cs = colorSpaces.get( colorSpaceName );
        }
        else if( colorSpaceName.equals( PDLab.NAME ) )
        {
            cs = new PDLab();
        }
        else if( colorSpaceName.equals( PDPattern.NAME ) )
        {
            cs = new PDPattern();
        }
        else
        {
            throw new IOException( "Error: Unknown colorspace '" + colorSpaceName + "'" );
        }
        return cs;
    }

    /**
     * This will create the correct color space from a java colorspace.
     *
     * @param doc The doc to potentiall write information to.
     * @param cs The awt colorspace.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
/**
 * This will create the correct color space from a java colorspace.
 *
 * @param doc
 * 		The doc to potentiall write information to.
 * @param cs
 * 		The awt colorspace.
 * @return The color space.
 * @throws IOException
 * 		If the color space name is unknown.
 */
public static org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace createColorSpace(org.apache.pdfbox.pdmodel.PDDocument doc, java.awt.color.ColorSpace cs) throws java.io.IOException {
    org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace retval = null;
    if (cs.isCS_sRGB()) {
        retval = org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB.INSTANCE;
    } else if (cs instanceof java.awt.color.ICC_ColorSpace) {
        java.awt.color.ICC_ColorSpace ics = ((java.awt.color.ICC_ColorSpace) (cs));
        org.apache.pdfbox.pdmodel.graphics.color.PDICCBased pdCS = new org.apache.pdfbox.pdmodel.graphics.color.PDICCBased(doc);
        retval = pdCS;
        org.apache.pdfbox.cos.COSArray ranges = new org.apache.pdfbox.cos.COSArray();
        for (int i = 0; i < cs.getNumComponents(); i++) {
            ranges.add(new org.apache.pdfbox.cos.COSFloat(ics.getMinValue(i)));
            ranges.add(new org.apache.pdfbox.cos.COSFloat(ics.getMaxValue(i)));
        }
        org.apache.pdfbox.pdmodel.common.PDStream iccData = pdCS.getPDStream();
        java.io.OutputStream output = null;
        try {
            /* NPEX_PATCH_BEGINS */
            output = (iccData != null) ? iccData.createOutputStream() : null;
            output.write(ics.getProfile().getData());
        } finally {
            if (output != null) {
                output.close();
            }
        }
        pdCS.setNumberOfComponents(cs.getNumComponents());
    } else {
        throw new java.io.IOException("Not yet implemented:" + cs);
    }
    return retval;
}
}
