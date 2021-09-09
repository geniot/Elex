package io.github.geniot.elex.tools.decompile.lsd;

import java.util.ArrayList;
import java.util.List;

public class ArticleHeading {
    int reference;
    int next_reference;
    public List<Heading> headings;

    public ArticleHeading() {
        this.headings = new ArrayList<>();
        this.reference = 0;
        // refernce for next arcticle, used in the x6 for encoding
        this.next_reference = 0;
    }

    public String read(Decoder lsd_decoder, BitStream bstr, String known_prefix) {
        Heading h = new Heading();
        int prefix_len = lsd_decoder.decode_prefix_len();
        int postfix_len = lsd_decoder.decode_postfix_len();
        h.text = known_prefix.substring(0, prefix_len);
        h.text += lsd_decoder.decode_heading(postfix_len);
        this.reference = lsd_decoder.read_reference2();
        if (bstr.read_bit()) {
            // additional not visible formatting item in header
            // join multisymbols item
            int ext_length = bstr.read_bits(8);
            if (ext_length != 0) {
                String ext = "";
                int first_idx = 0;
                int prev_idx = 0;
                for (int i = 0; i < ext_length; i++) {
                    int idx = bstr.read_bits(8);
//                    String char_ = int2unichr(ByteBuffer.allocate(4).putInt(bstr.read_bits(16)).array());
                    char char_ = (char) bstr.read_bits(16);
                    if (ext.equals("")) {
                        ext += char_;
                        first_idx = idx;
                        prev_idx = idx;
                    } else {
                        if (prev_idx + 1 == idx) {
                            // join item with sequence idx
                            ext += char_;
                            prev_idx = idx;
                        } else {
                            // other item
                            h.extensions.put(first_idx, ext);
                            ext = String.valueOf(char_);
                            first_idx = idx;
                            prev_idx = idx;
                        }
                        // add last item
                        h.extensions.put(first_idx, ext);
                    }
                }
            }
        }
        this.headings.add(h);
        return h.text;
    }

    public void merge(ArticleHeading h) {
        for (Heading heading : h.headings) {
            this.headings.add(heading);
        }
    }

    Heading get_first() {
        return this.headings.get(0);
    }

    public String get_first_ext_text() {
        return this.headings.get(0).ext_text();
    }

    public boolean simple() {
        return this.headings.size() == 1;
    }

    void dump() {
        System.out.println(this.get_first().text + " " + this.reference + " " + this.next_reference);
    }
}
