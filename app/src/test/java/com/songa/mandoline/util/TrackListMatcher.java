package com.songa.mandoline.util;

import com.songa.mandoline.audio.entity.Track;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import java.util.List;

public class TrackListMatcher extends ArgumentMatcher<List<Track>>
{
    private final List<Track> expected;

    public TrackListMatcher(List<Track> expected) {
        super();
        this.expected = expected;
    }

    @Override
    public void describeTo(Description description) {
        super.describeTo(description);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object argument) {
        List<Track> arg = (List<Track>)argument;
        if (arg.size()!=expected.size()) { return false; }
        for (int i=0; i<expected.size(); i++) {
            if (expected.get(i)==null) {
                if (arg.get(i)!=null) { return false; }
            } else {
                if (!expected.get(i).equals(arg.get(i))) { return false; }
            }
        }
        return true;
    }
}