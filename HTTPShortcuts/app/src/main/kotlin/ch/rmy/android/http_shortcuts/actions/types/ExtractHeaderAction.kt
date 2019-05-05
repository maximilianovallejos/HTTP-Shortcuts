package ch.rmy.android.http_shortcuts.actions.types

import android.content.Context
import ch.rmy.android.http_shortcuts.R
import ch.rmy.android.http_shortcuts.data.Controller
import ch.rmy.android.http_shortcuts.extensions.toPromise
import ch.rmy.android.http_shortcuts.http.ShortcutResponse
import ch.rmy.android.http_shortcuts.utils.PromiseUtils
import ch.rmy.android.http_shortcuts.variables.VariablePlaceholderProvider
import ch.rmy.android.http_shortcuts.variables.Variables
import com.android.volley.VolleyError
import org.jdeferred2.Promise

class ExtractHeaderAction(
    id: String,
    actionType: ExtractHeaderActionType,
    data: Map<String, String>
) : BaseAction(id, actionType, data) {

    var headerKey: String
        get() = internalData[KEY_HEADER_KEY] ?: ""
        set(value) {
            internalData[KEY_HEADER_KEY] = value
        }

    var variableId: String
        get() = internalData[KEY_VARIABLE_ID] ?: ""
        set(value) {
            internalData[KEY_VARIABLE_ID] = value
        }

    override fun getDescription(context: Context): CharSequence =
        context.getString(R.string.action_type_extract_header_description, headerKey, Variables.toRawPlaceholder(variableId))

    override fun perform(context: Context, shortcutId: String, variableValues: MutableMap<String, String>, response: ShortcutResponse?, volleyError: VolleyError?, recursionDepth: Int): Promise<Unit, Throwable, Unit> {
        val headerValue = response?.headers?.get(headerKey)
            ?: volleyError?.networkResponse?.headers?.get(headerKey)
            ?: return PromiseUtils.resolve(Unit)

        variableValues[variableId] = headerValue
        Controller().use { controller ->
            return controller.setVariableValue(variableId, headerValue).toPromise()
        }
    }

    override fun createEditorView(context: Context, variablePlaceholderProvider: VariablePlaceholderProvider) =
        ExtractHeaderActionEditorView(context, this, variablePlaceholderProvider)

    companion object {

        private const val KEY_HEADER_KEY = "headerKey"
        private const val KEY_VARIABLE_ID = "variableId"

    }

}