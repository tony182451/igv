/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2007-2015 Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.broad.igv.feature.aa;

import org.broad.igv.feature.Strand;

import java.util.List;

/**
 * Represents an amino acid sequence for an exon
 *
 * @author jrobinso
 */
public class AminoAcidSequence {

    private final Strand strand;
    private final int start;                // Genomic position for start of sequence.
    private final List<CodonAA> sequence;

    private boolean nonNullSequence;

    /**
     * If the sequence was computed from a nucleotide sequence,
     * we store how the calculation was performed
     */
    private final Integer id;

    public AminoAcidSequence(Strand strand, int startPosition,
                             List<CodonAA> sequence,
                             Integer codonTableKey) {
        this.strand = strand;
        this.start = startPosition;
        this.sequence = sequence;
        this.id = codonTableKey;

        // Look for a non null sequence.  Sequences are null if the sequence
        // directory is undefined or unreachable.  
        nonNullSequence = false;
        for (CodonAA aa : sequence) {
            if (aa != null) {
                nonNullSequence = true;
                break;
            }
        }

    }

    public Strand getStrand() {
        return strand;
    }

    public int getStart() {
        return start;
    }

    public List<CodonAA> getSequence() {
        return sequence;
    }

    public boolean hasNonNullSequence() {
        return nonNullSequence;
    }

    public Integer getId() {
        return id;
    }

    /**
     * Return the codon sequence as a string -- primarily for debugging.
     * @return
     */
    public String getSequenceString() {
        String ss = "";
        for(CodonAA c : sequence) {
            ss += c.getAminoAcid().getSymbol();
        }
        return ss;
    }
}
