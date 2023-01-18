package com.adiacent.menarini.menarinimaster.core.utils;

import org.apache.sling.api.resource.Resource;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.ArrayList;
import java.util.List;

public class CustomNodeIterator implements NodeIterator {
    private int position = 0;
    private List<Resource> resourceList;

    public CustomNodeIterator(List<Resource> resourceList){
        this.resourceList = resourceList;
        if(this.resourceList == null){
            this.resourceList = new ArrayList<>();
        }
    }

    @Override
    public Node nextNode() {
        if(position < resourceList.size()){
            return resourceList.get(position++).adaptTo(Node.class);
        }
        return null;
    }

    @Override
    public void skip(long l) {
        if((position + l) < resourceList.size()){
            position = position + (int)l;
        }
    }

    @Override
    public long getSize() {
        return resourceList.size();
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public boolean hasNext() {
        if(position == 0 && !resourceList.isEmpty()){
            return true;
        }else{
            return (position + 1 ) <= resourceList.size();
        }
    }

    @Override
    public Object next() {
        return nextNode();
    }
}
