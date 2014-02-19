import org.mixer2.jaxb.xhtml.*
import org.mixer2.xhtml.*

def html = html as Html
def helloMessage = model.get("helloMessage") as String

Div messageDiv = html.getBody().getById("message")
messageDiv.unsetContent()
messageDiv.getContent().add(helloMessage + " using groovy.")

