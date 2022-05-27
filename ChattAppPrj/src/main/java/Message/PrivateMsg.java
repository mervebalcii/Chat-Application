/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Message;

/**
 *
 * @author Furu
 */
public class PrivateMsg implements java.io.Serializable {
    private String sender;
    private String target;
    private String content;
    // ozel mesaj nesnesi
    public PrivateMsg(String sender, String target, String content) {
        this.sender = sender;
        this.target = target;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
